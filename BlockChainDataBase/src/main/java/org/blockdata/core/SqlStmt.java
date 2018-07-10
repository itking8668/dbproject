package org.blockdata.core;

public class SqlStmt {
    public static String metaDataStmt = "drop table if exists metadata ; CREATE TABLE metadata" +
            "(chainId INT PRIMARY KEY NOT NULL," +
            " blockInterval      INT ,"+
            " genesisTimestamp   CHAR(32) ,"+
            " chainName   CHAR(256) ,"+
            " operator   CHAR(256) ,"+
            " website   CHAR(256) ,"+
            " tokenName   CHAR(256) ,"+
            " tokenSymbol   CHAR(256) ,"+
            " tokenAvatar   CHAR(256),"+
            " validators   TEXT);";

    public static String blockStmt = "drop table if exists block ; CREATE TABLE block" +
            "(number BIGINT PRIMARY KEY NOT NULL," +
            " timestamp      BIGINT ,"+
            " blockhash      CHAR(256) NOT NULL, " +
            " version        CHAR(16), " +
            " prevhash       CHAR(256), " +
            " stateRoot      CHAR(256), " +
            " transactionsRoot CHAR(256), " +
            " receiptsRoot   CHAR(256), " +
            " gasused        CHAR(32)," +
            " proposer       CHAR(256),"+
            " proposal       CHAR(256),"+
            " height         CHAR(256),"+
            " round          CHAR(256));";
    //commits
    public static String blockIndexStmt = "CREATE UNIQUE INDEX blockindex ON block (number);";

    public static String transactionStmt = "drop table if exists blocktransaction;CREATE TABLE blocktransaction" +
            "(number BIGINT, " +
            " transactionHash   CHAR(256)," +
            " transactionIndex  CHAR(32) ,"+
            " blockHash         CHAR(256) ,"+
            " cumulativeGasUsed CHAR(32) ,"+
            " gasUsed           CHAR(32) ,"+
            " contractAddress   CHAR(256) ,"+
            " root              CHAR(256) ,"+
            " content           TEXT ,"+
            " toAddresss         CHAR(256));";
    public static String transactionIndexStmt = "CREATE UNIQUE INDEX transactionindex ON blocktransaction (number,transactionHash);";
}
