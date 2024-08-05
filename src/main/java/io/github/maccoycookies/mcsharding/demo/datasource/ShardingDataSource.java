package io.github.maccoycookies.mcsharding.demo.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import io.github.maccoycookies.mcsharding.demo.engine.ShardingContext;
import io.github.maccoycookies.mcsharding.demo.engine.ShardingResult;
import io.github.maccoycookies.mcsharding.demo.config.ShardingProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * sharding datasource
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/4 11:05
 */

public class ShardingDataSource extends AbstractRoutingDataSource {

    public ShardingDataSource(ShardingProperties shardingProperties) {
        Map<Object, Object> datasourceMap = new LinkedHashMap<>();
        Map<String, Properties> datasources = shardingProperties.getDatasources();

        datasources.forEach((key, value) -> {
            try {
                datasourceMap.put(key, DruidDataSourceFactory.createDataSource(value));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
        setTargetDataSources(datasourceMap);
        setDefaultTargetDataSource(datasourceMap.values().iterator().next());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult shardingResult = ShardingContext.get();
        String key = shardingResult == null ? null : shardingResult.getTargetDataSourceName();
        System.out.println("===> determineCurrentLookupKey = " + key);
        return key;
    }
}
