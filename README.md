# Goku JDBC

This is an Oracle JDBC DataSource wrapper.
It replace the SQLException message with `オッス ORA-XXXXX` when there is a corresponding error.

```java
GokuDataSource ds = new GokuDataSource();
ds.setConnectionFactoryName("oracle.jdbc.pool.OracleDataSource");
ds.setURL("jdbc:oracle:thin:@localhost:1521:xe");
ds.setUser("scott");
ds.setPassword("tiger");
try (Connection conn = ds.getConnection();
     Statement stmt = conn.createStatement()) {
    stmt.execute("SELECT hoge FROM huga");
}
```

```
net.unit8.goku.GokuSQLException: オッス ORA-00942

```