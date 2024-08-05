package io.github.maccoycookies.mcsharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;

    private String name;

    private int age;

}

