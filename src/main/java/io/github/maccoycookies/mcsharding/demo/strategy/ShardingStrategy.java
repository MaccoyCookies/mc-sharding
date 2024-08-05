package io.github.maccoycookies.mcsharding.demo.strategy;

import java.util.List;
import java.util.Map;

/**
 * Description for this class
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/5 23:55
 */
public interface ShardingStrategy {

    List<String> getShardingColumns();

    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);

}
