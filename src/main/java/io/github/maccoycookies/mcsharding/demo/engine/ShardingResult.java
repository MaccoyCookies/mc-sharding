package io.github.maccoycookies.mcsharding.demo.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sharding result
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/4 11:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShardingResult {

    private String targetDataSourceName;

}
