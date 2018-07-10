package org.blockdata.db;

import java.sql.ResultSet;

public interface Db {
    public int initDb(String dbName) throws Exception;
    public int execSql(String sql) throws Exception;
    public ResultSet execQuery(String sql) throws Exception;
}
