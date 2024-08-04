package io.github.maccoycookies.mcsharding;

import io.github.maccoycookies.mcsharding.demo.User;
import io.github.maccoycookies.mcsharding.demo.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "io.github.maccoycookies.mcsharding.demo", factoryBean = ShardingMapperFactoryBean.class)
public class McShardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(McShardingApplication.class, args);
    }

    @Autowired
    UserMapper userMapper;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {

            int id = 3;

            System.out.println("===> 1. test insert ...");
            int inserted = userMapper.insert(new User(id, "maccoy", 19));
            System.out.println("===> inserted = " + inserted);

            System.out.println("===> 2. test find ...");
            User user = userMapper.findById(id);
            System.out.println("===> find = " + user);

            System.out.println("===> 3. test update ...");
            user.setName("cookie");
            int updated = userMapper.update(user);
            System.out.println("===> updated = " + updated);

            System.out.println("===> 4. test new find ...");
            User user2 = userMapper.findById(id);
            System.out.println("===> find = " + user2);

            System.out.println("===> 5. test delete ...");
            int deleted = userMapper.delete(user2.getId());
            System.out.println("===> deleted = " + deleted);
        };
    }

}
