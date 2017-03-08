package net.unit8.goku;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kawasima
 */
public class GokuInvocationHandler implements InvocationHandler {
    private final Object orig;

    private static Set<Class<?>> WRAP_TARGET_CLASSES = new HashSet<>(
            Arrays.asList(
                    Connection.class,
                    Statement.class,
                    PreparedStatement.class,
                    CallableStatement.class,
                    ResultSet.class)
    );

    GokuInvocationHandler(Object orig) {
        this.orig = orig;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Object ret = method.invoke(orig, args);
            Class<?> returnType = method.getReturnType();
            if (WRAP_TARGET_CLASSES.contains(returnType)) {
                ret = Proxy.newProxyInstance(returnType.getClassLoader(), new Class[]{returnType},
                        new GokuInvocationHandler(ret));
            }
            return ret;
        } catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof SQLException) {
                targetEx = new GokuSQLException((SQLException) targetEx);
            }
            throw targetEx;
        }
    }
}
