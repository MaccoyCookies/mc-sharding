package io.github.maccoycookies.mcsharding.demo.strategy;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * hash sharding strategy
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/5 23:56
 */
public class HashShardingStrategy implements ShardingStrategy {

    private final String shardingColumn;
    private final String algorithmExpression;

    public HashShardingStrategy(Properties properties) {
        this.shardingColumn = properties.getProperty("shardingColumn");
        this.algorithmExpression = properties.getProperty("algorithmExpression");
    }

    @Override
    public List<String> getShardingColumns() {
        return List.of(shardingColumn);
    }

    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        String express = InlineExpressionParser.handlePlaceHolder(algorithmExpression);
        InlineExpressionParser parser = new InlineExpressionParser(express);
        Closure closure = parser.evaluateClosure();
        closure.setProperty(this.shardingColumn, shardingParams.get(this.shardingColumn));
        return closure.call().toString();
    }
}
