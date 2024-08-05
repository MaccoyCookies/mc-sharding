package io.github.maccoycookies.mcsharding.demo.mybatis;

import io.github.maccoycookies.mcsharding.demo.engine.ShardingContext;
import io.github.maccoycookies.mcsharding.demo.engine.ShardingResult;
import io.github.maccoycookies.mcsharding.demo.model.User;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Proxy;

/**
 * Factory bean for mapper
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/4 11:46
 */
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public ShardingMapperFactoryBean() {

    }

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        SqlSession sqlSession = getSqlSession();
        Configuration configuration = sqlSession.getConfiguration();
        Class<T> clazz = getMapperInterface();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                (p, method, args) -> {
                    // 获取分片信息
                    // TODO sharding engine
                    String mapperId = clazz.getName() + "." + method.getName();
                    MappedStatement mappedStatement = configuration.getMappedStatement(mapperId);
                    BoundSql boundSql = mappedStatement.getBoundSql(args);
                    System.out.println("===> getObject sql statement: " + boundSql.getSql());
                    Object parameterObject = args[0];
                    if (parameterObject instanceof User user) {
                        ShardingContext.set(new ShardingResult(user.getId() % 2 == 0 ? "ds0" : "ds1"));
                    } else if (parameterObject instanceof Integer id) {
                        ShardingContext.set(new ShardingResult(id % 2 == 0 ? "ds0" : "ds1"));
                    }
                    return method.invoke(proxy, args);
                });
    }
}
