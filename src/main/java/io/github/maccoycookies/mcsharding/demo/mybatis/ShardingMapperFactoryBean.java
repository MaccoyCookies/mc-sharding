package io.github.maccoycookies.mcsharding.demo.mybatis;

import io.github.maccoycookies.mcsharding.demo.engine.ShardingContext;
import io.github.maccoycookies.mcsharding.demo.engine.ShardingEngine;
import io.github.maccoycookies.mcsharding.demo.engine.ShardingResult;
import io.github.maccoycookies.mcsharding.demo.model.User;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Factory bean for mapper
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/4 11:46
 */
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    @Setter
    ShardingEngine shardingEngine;

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

                    Object[] params = getParams(boundSql, args);
                    ShardingResult shardingResult = shardingEngine.sharding(boundSql.getSql(), params);
                    ShardingContext.set(shardingResult);
                    return method.invoke(proxy, args);
                });
    }

    @SneakyThrows
    private Object[] getParams(BoundSql boundSql, Object[] args) {
        Object[] params = args;
        if (args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            Object arg = args[0];
            List<String> cols = boundSql.getParameterMappings().stream().map(ParameterMapping::getProperty).toList();
            Object[] newParams = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                newParams[i] = getFieldValue(arg, cols.get(i));
            }
            params = newParams;
        }

        return params;
    }

    private static Object getFieldValue(Object arg, String col) throws NoSuchFieldException, IllegalAccessException {
        Field field = arg.getClass().getDeclaredField(col);
        field.setAccessible(true);
        return field.get(arg);
    }
}
