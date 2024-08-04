package io.github.maccoycookies.mcsharding;

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


}
