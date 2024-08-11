package io.github.maccoycookies.mcsharding.demo.mybatis;

import io.github.maccoycookies.mcsharding.demo.engine.ShardingContext;
import io.github.maccoycookies.mcsharding.demo.engine.ShardingResult;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Description for this class
 *
 * @Author : maccoy(maccoy@foxmail.com)
 * @create 2024/8/4 11:29
 */
@Intercepts(
        @org.apache.ibatis.plugin.Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {java.sql.Connection.class, Integer.class}
        )
)
public class SqlStatementInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        ShardingResult shardingResult = ShardingContext.get();
        if (shardingResult != null) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            System.out.println("===> sql statement: " + boundSql.getSql());
            System.out.println("===> sql parameters: " + boundSql.getParameterObject());
            // TODO 修改sql user -> user1
            if (!boundSql.getSql().equalsIgnoreCase(shardingResult.getTargetSqlStatement())) {
                replaceSql(boundSql, shardingResult);
            }
        }
        return invocation.proceed();
    }

    private static void replaceSql(BoundSql boundSql, ShardingResult shardingResult) throws NoSuchFieldException {
        Field field = boundSql.getClass().getDeclaredField("sql");
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        long fieldOffset = unsafe.objectFieldOffset(field);
        unsafe.putObject(boundSql, fieldOffset, shardingResult.getTargetSqlStatement());
    }
}






