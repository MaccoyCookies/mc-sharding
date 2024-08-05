package io.github.maccoycookies.mcsharding.demo.engine;

/**
 * Sharding context
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/4 11:14
 */
public class ShardingContext {

    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();


    public static ShardingResult get() {
        return LOCAL.get();
    }

    public static void set(ShardingResult shardingResult) {
        LOCAL.set(shardingResult);
    }
}
