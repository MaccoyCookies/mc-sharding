package io.github.maccoycookies.mcsharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/11 11:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private int id;

    private int uid;

    private double price;


}
