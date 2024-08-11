package io.github.maccoycookies.mcsharding.demo.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import io.github.maccoycookies.mcsharding.demo.config.ShardingProperties;
import io.github.maccoycookies.mcsharding.demo.strategy.HashShardingStrategy;
import io.github.maccoycookies.mcsharding.demo.strategy.ShardingStrategy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/5 23:41
 */
public class StandardShardingEngine implements ShardingEngine {

    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
    private final Map<String, ShardingStrategy> tableStrategies = new HashMap<>();


    public StandardShardingEngine(ShardingProperties shardingProperties) {
        shardingProperties.getTables().forEach((table, tableProperties) -> {
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0], tableName = split[1];
                actualDatabaseNames.add(databaseName, tableName);
                actualTableNames.add(tableName, databaseName);
            });
            databaseStrategies.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategies.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));
        });

    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {

        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        String tableName;
        Map<String, Object> shardingColumnsMap = new LinkedHashMap<>();
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            tableName = sqlInsertStatement.getTableName().getSimpleName();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) columns.get(i);
                String columnNameStr = columnExpr.getSimpleName();
                shardingColumnsMap.put(columnNameStr, args[i]);
            }
        } else {
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);

            LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if (sqlNames.size() > 1) {
                throw new RuntimeException("not support multi table sharding: " + sqlNames);
            }
            tableName = sqlNames.iterator().next().getSimpleName();
            System.out.println("===> visitor.getOriginalTables = " + tableName);
            shardingColumnsMap = visitor.getConditions().stream().collect(Collectors.toMap(c -> c.getColumn().getName(), c -> c.getValues().get(0)));
        }
        ShardingStrategy databaseStrategy = databaseStrategies.get(tableName);
        String actualDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(tableName), tableName, shardingColumnsMap);
        ShardingStrategy tableStrategy = tableStrategies.get(tableName);
        String actualTable = tableStrategy.doSharding(actualTableNames.get(tableName), tableName, shardingColumnsMap);
        System.out.println("===> actual db.table = " + actualDatabase + "." + actualTable);
        return new ShardingResult(actualDatabase, sql.replace(tableName, actualTable));
    }
}
