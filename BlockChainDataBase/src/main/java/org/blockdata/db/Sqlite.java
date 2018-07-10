package org.blockdata.db;

import java.sql.*;

public class Sqlite implements Db {

    private Connection sqliteCon = null;

    public Sqlite(String dbName) throws Exception{
        initDb(dbName);
    }

    public Connection getConnection(){
        return sqliteCon;
    }

    public int initDb(String dbName) throws Exception{
        Class.forName("org.sqlite.JDBC");
        sqliteCon = DriverManager.getConnection("jdbc:sqlite:"+dbName);
        return 0;
    }

    public int execSql(String sql) throws Exception {
        int ret = 0;
        Statement stmt = sqliteCon.createStatement();
        ret = stmt.executeUpdate(sql);
        stmt.close();
        return ret;
    }

    public ResultSet execQuery(String sql) throws Exception {
        Statement stmt = sqliteCon.createStatement();
        ResultSet rs = stmt.executeQuery( sql );
        return rs;
    }


    public static void main( String args[] )
    {
        try{
            int ret = 0;
            Sqlite sqlite = new Sqlite("blockchain.db");
            if (ret != 0){
                System.out.print("init db error" + ret);
                return ;
            }

            String sql = "drop table if exists block; CREATE TABLE block " +
                    "(num BIGINT PRIMARY KEY     NOT NULL," +
                    " hash           CHAR(256)    NOT NULL, " +
                    " version        CHAR(16), " +
                    " prevHash        CHAR(256), " +
                    " gasUsed         CHAR(32))";

            sqlite.execSql(sql);
            sql = "insert into block values(1,'0x123456', '0.1','0x4321','0x32');";
            sqlite.execSql(sql);
            sql = "select * from block;";
            ResultSet rs = sqlite.execQuery(sql);
            while ( rs.next() ) {
                int num = rs.getInt("num");
                String  hash = rs.getString("hash");
                String  version = rs.getString("version");
                String  prevHash = rs.getString("prevHash");
                String  gasUsed = rs.getString("gasUsed");

                System.out.println( "num = " + num );
                System.out.println( "hash = " + hash );
                System.out.println( "version = " + version );
                System.out.println( "prevHash = " + prevHash );
                System.out.println( "gasUsed = " + gasUsed );
                System.out.println();
            }
            rs.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
