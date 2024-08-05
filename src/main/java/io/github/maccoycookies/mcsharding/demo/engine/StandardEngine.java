package io.github.maccoycookies.mcsharding.demo.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import io.github.maccoycookies.mcsharding.demo.config.ShardingProperties;
import io.github.maccoycookies.mcsharding.demo.model.User;
import io.github.maccoycookies.mcsharding.demo.mybatis.SqlStatementInterceptor;
import io.github.maccoycookies.mcsharding.demo.strategy.HashShardingStrategy;
import io.github.maccoycookies.mcsharding.demo.strategy.ShardingStrategy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description for this class
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/5 23:41
 */
public class StandardEngine implements ShardingEngine {

    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
    private final Map<String, ShardingStrategy> tableStrategies = new HashMap<>();


    public StandardEngine(ShardingProperties shardingProperties) {
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
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            String tableName = sqlInsertStatement.getTableName().getSimpleName();
            Map<String, Object> shardingColumnMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) columns.get(i);
                String columnNameStr = columnExpr.getSimpleName();
                shardingColumnMap.put(columnNameStr, args[i]);
            }
            ShardingStrategy databaseStrategy = databaseStrategies.get(tableName);
            String actualDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(tableName), tableName, shardingColumnMap);
            ShardingStrategy tableStrategy = tableStrategies.get(tableName);
            String actualTable = tableStrategy.doSharding(actualTableNames.get(tableName), tableName, shardingColumnMap);

            System.out.println("===> actual db.table = " + actualDatabase + "." + actualTable);
        }


        Object parameterObject = args[0];
        System.out.println("===> getObject sql statement: " + sql);
        int id = 0;
        if (parameterObject instanceof User user) {
            id = user.getId();
        } else if (parameterObject instanceof Integer uid) {
            id = uid;
        }

        return new ShardingResult(id % 2 == 0 ? "ds0" : "ds1", sql);

    }
}
