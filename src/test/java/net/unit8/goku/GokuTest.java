package net.unit8.goku;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author kawasima
 */
public class GokuTest {
    @Test
    public void test() throws SQLException {
        GokuDataSource ds = new GokuDataSource();
        ds.setConnectionFactoryName("oracle.jdbc.pool.OracleDataSource");
        ds.setURL("jdbc:oracle:thin:@localhost:1521:xe");
        ds.setUser("scott");
        ds.setPassword("tiger");
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT hoge FROM huga");
        }
    }
}
