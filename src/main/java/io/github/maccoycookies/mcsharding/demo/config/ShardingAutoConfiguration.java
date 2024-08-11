package io.github.maccoycookies.mcsharding.demo.config;

import io.github.maccoycookies.mcsharding.demo.datasource.ShardingDataSource;
import io.github.maccoycookies.mcsharding.demo.engine.ShardingEngine;
import io.github.maccoycookies.mcsharding.demo.engine.StandardShardingEngine;
import io.github.maccoycookies.mcsharding.demo.mybatis.SqlStatementInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sharding auto configuration
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/4 11:19
 */
@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties shardingProperties) {
        return new ShardingDataSource(shardingProperties);
    }

    @Bean
    public ShardingEngine shardingEngine(ShardingProperties shardingProperties) {
        return new StandardShardingEngine(shardingProperties);
    }

    @Bean
    public SqlStatementInterceptor sqlStatementInterceptor() {
        return new SqlStatementInterceptor();
    }


}
