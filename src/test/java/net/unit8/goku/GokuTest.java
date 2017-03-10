package net.unit8.goku;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author kawasima
 */
public class GokuTest {
    private static final Logger LOG = LoggerFactory.getLogger(GokuTest.class);

    @Test
    public void test942() throws SQLException {
        GokuDataSource ds = new GokuDataSource();
        ds.setConnectionFactoryName("oracle.jdbc.pool.OracleDataSource");
        ds.setURL("jdbc:oracle:thin:@localhost:1521:xe");
        ds.setUser("scott");
        ds.setPassword("tiger");
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT hoge FROM huga");
            fail();
        } catch(SQLException e) {
            LOG.error("", e);
            assertTrue(e.getMessage().contains("オッス ORA-00942"));
        }
    }

    @Test
    public void test1() throws SQLException {
        GokuDataSource ds = new GokuDataSource();
        ds.setConnectionFactoryName("oracle.jdbc.pool.OracleDataSource");
        ds.setURL("jdbc:oracle:thin:@localhost:1521:xe");
        ds.setUser("scott");
        ds.setPassword("tiger");
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE goku(A VARCHAR2(255) PRIMARY KEY)");
            stmt.executeUpdate("INSERT INTO goku(A) values ('KURIRIN')");
            stmt.executeUpdate("INSERT INTO goku(A) values ('KURIRIN')");
            conn.commit();
            fail();
        } catch(SQLException e) {
            LOG.error("", e);
            assertTrue(e.getMessage().contains("オッス ORA-00001"));
        } finally {
            try (Connection conn = ds.getConnection();
                 Statement stmt = conn.createStatement()
            ) {
                stmt.execute("DROP TABLE goku");
            }
        }
    }

    @Test
    public void testOther() throws SQLException {
        GokuDataSource ds = new GokuDataSource();
        ds.setConnectionFactoryName("oracle.jdbc.pool.OracleDataSource");
        ds.setURL("jdbc:oracle:thin:@localhost:1521:xe");
        ds.setUser("scott");
        ds.setPassword("tiger");
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE goku(A VARCHAR2(255) PRIMARY KEY)");
            stmt.executeUpdate("INSERT INTO goku(A) values ('KURIRIN')");
            ResultSet rs = stmt.executeQuery("SELECT * FROM goku");
            assertTrue(rs.next());
            rs.getString("B");
            fail();
        } catch(SQLException e) {
            LOG.error("", e);
            assertTrue(e.getMessage().contains("オッス ORA-17006"));
        } finally {
            try (Connection conn = ds.getConnection();
                 Statement stmt = conn.createStatement()
            ) {
                stmt.execute("DROP TABLE goku");
            }
        }
    }
}
