package net.unit8.goku;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author kawasima
 */
public class GokuDataSource implements DataSource{
    private String connectionFactoryName;
    private Properties connectionFactoryProperties;
    private String url;
    private String user;
    private String password;

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Class<DataSource> factoryClass = (Class<DataSource>) Class.forName(connectionFactoryName);
            Constructor<DataSource> constructor = factoryClass.getDeclaredConstructor();
            DataSource ds = constructor.newInstance();
            Class<? extends DataSource> dsClass = ds.getClass();
            dsClass.getMethod("setURL", String.class).invoke(ds, url);
            dsClass.getMethod("setUser", String.class).invoke(ds, user);
            dsClass.getMethod("setPassword", String.class).invoke(ds, password);
            Connection conn = ds.getConnection();
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class},
                    new GokuInvocationHandler(conn));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SQLException(ex.getMessage());
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
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
