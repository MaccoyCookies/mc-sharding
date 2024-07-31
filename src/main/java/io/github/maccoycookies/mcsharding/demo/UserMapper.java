package io.github.maccoycookies.mcsharding.demo;

import org.apache.ibatis.annotations.*;

/**
 * mapper for user
 */
@Mapper
public interface UserMapper {

    @Insert("insert into user (id, name, age) values (#{id}, #{name}, #{age})")
    int insert(User user);

    @Select("select * from user where id = #{id}")
    User findById(@Param("id") int id);

    @Update("update user set name = #{name}, age = #{age} where id = #{id}")
    int update(User user);

    @Delete("delete from user where id = #{id}")
    int delete(int id);

}
