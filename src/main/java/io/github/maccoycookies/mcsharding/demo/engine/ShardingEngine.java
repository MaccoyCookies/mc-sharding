package io.github.maccoycookies.mcsharding.demo.engine;

/**
 * Core sharding engine.
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/5 23:38
 */
public interface ShardingEngine {

    ShardingResult sharding(String sql, Object[] args);

}
