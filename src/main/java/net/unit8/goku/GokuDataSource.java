package net.unit8.goku;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author kawasima
 */
public class GokuDataSource implements DataSource{
    private PrintWriter logWriter;
    private String connectionFactoryName;
    private Properties connectionFactoryProperties;
    private String url;
    private String user;
    private String password;
    private int loginTimeout;

    private <T> void setProperty(DataSource ds, String setterName, Class<T> type, T value) throws SQLException {
        Class<? extends DataSource> dsClass = ds.getClass();
        try {
            Method setter = dsClass.getMethod(setterName, type);
            setter.invoke(ds, value);
        } catch (NoSuchMethodException e) {
            throw new SQLException(String.format(Locale.US, "DataSource must have a %s method", setterName));
        } catch (Exception e) {
            throw new SQLException("DataSource property error: " + e.getMessage());
        }
    }

    private DataSource createDataSource(String username, String password) throws SQLException {
        try {
            Class<DataSource> factoryClass = (Class<DataSource>) Class.forName(connectionFactoryName);
            Constructor<DataSource> constructor = factoryClass.getDeclaredConstructor();
            DataSource ds = constructor.newInstance();
            setProperty(ds, "setURL", String.class, url);
            setProperty(ds, "setUser", String.class, username);
            setProperty(ds, "setPassword", String.class, password);
            return ds;
        } catch (ClassNotFoundException e) {
            throw new SQLException(String.format(Locale.US, "ConnectionFactory `%s` is not found", connectionFactoryName));
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException(String.format(Locale.US, "something's wrong"));
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            DataSource ds = createDataSource(this.user, this.password);
            Connection conn = ds.getConnection();
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class},
                    new GokuInvocationHandler(conn));
        } catch (Exception ex) {
            throw new SQLException(ex.getMessage());
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            DataSource ds = createDataSource(username, password);
            Connection conn = ds.getConnection();
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class},
                    new GokuInvocationHandler(conn));
        } catch (Exception ex) {
            throw new SQLException(ex.getMessage());
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.equals(DataSource.class)) {
            return (T) this;
        } else {
            throw new SQLException(String.format(Locale.US, "Type mismatch: expected %s was %s", DataSource.class, iface));
        }

    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        if (!"oracle.jdbc.pool.OracleDataSource".equals(connectionFactoryName)) {
            throw new IllegalArgumentException("Must be `OracleDataSource`");
        }
        this.connectionFactoryName = connectionFactoryName;
    }

    public Properties getConnectionFactoryProperties() {
        return connectionFactoryProperties;
    }

    public void setConnectionFactoryProperties(Properties connectionFactoryProperties) {
        this.connectionFactoryProperties = connectionFactoryProperties;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
