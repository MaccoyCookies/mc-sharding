package io.github.maccoycookies.mcsharding.demo.mapper;

import io.github.maccoycookies.mcsharding.demo.model.Order;
import io.github.maccoycookies.mcsharding.demo.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * mapper for user
 */
@Mapper
public interface OrderMapper {

    @Insert("insert into t_order (id, uid, price) values (#{id}, #{uid}, #{price})")
    int insert(Order order);

    @Select("select * from t_order where id = #{id} and uid = #{uid}")
    Order findById(@Param("id") int id, @Param("uid") int uid);

    @Update("update t_order set price = #{price} where id = #{id} and uid = #{uid}")
    int update(Order order);

    @Delete("delete from t_order where id = #{id} and uid = #{uid}")
    int delete(@Param("id") int id, @Param("uid") int uid);

}
