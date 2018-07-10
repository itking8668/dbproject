package org.blockdata.core;

import org.blockdata.chain.Chain;
import org.blockdata.chain.CitaChain;
import org.blockdata.data.Block;
import org.blockdata.data.MetaData;
import org.blockdata.data.Transaction;
import org.blockdata.db.Db;
import org.blockdata.db.Sqlite;

import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

public class BlockToDb {
    private Chain chain;
    private Db db;

    public BlockToDb(Db db,Chain ch) throws Exception{
        this.db = db;
        this.chain = ch;
    }

    public int createDatabase(String dbName) throws Exception {
        return db.initDb(dbName);
    }

    public int createBlockChainTable() throws Exception{
        int ret = 0;
        ret += db.execSql(SqlStmt.metaDataStmt);
        ret += db.execSql(SqlStmt.blockStmt);
        ret += db.execSql(SqlStmt.blockIndexStmt);
        ret += db.execSql(SqlStmt.transactionStmt);
        ret += db.execSql(SqlStmt.transactionIndexStmt);
        return ret;
    }

    public void getMetaData() throws Exception {
        MetaData metaData = chain.getMetaData();
        String sql = "select * from metadata where chainid = " + metaData.chainId +";";
        ResultSet rs = db.execQuery(sql);
        String validators = "";
        for (int j = 0; j < metaData.validators.length; j++) {
            validators = validators + metaData.validators[j] + ";";
        }
        if (rs.next() == false) {
            sql = "insert into metadata(chainId,blockInterval,genesisTimestamp,chainName,operator,website," +
                    "tokenName,tokenSymbol,tokenAvatar,validators) values(" +
                    metaData.chainId + "," +
                    metaData.blockInterval + ",'" +
                    metaData.genesisTimestamp + "','" +
                    metaData.chainName + "','" +
                    metaData.operator + "','" +
                    metaData.website + "','" +
                    metaData.tokenName + "','" +
                    metaData.tokenSymbol + "','" +
                    metaData.tokenAvatar + "','" +
                    validators + "'" +
                    ");";

            db.execSql(sql);
        }else{
            String dbValidators = rs.getString("validators");
            if(dbValidators.compareTo(validators) != 0 ) {
                sql = "update metadata set validators = '" + validators + "' where chainid = " + metaData.chainId;
                db.execSql(sql);
            }
        }
    }

    /*
    * Timing synchronization metadata, the frequency can be set to a larger interval.
    * */
    public void timeRunMetaData(int period){

        new Timer("timer:"+ period ).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getMetaData();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 0, period);

    }

    public void headToDb(Block block) throws  Exception{
        String sql = "insert into block(number,timestamp,blockhash,version,prevhash," +
                "stateRoot,transactionsRoot,receiptsRoot,gasused,proposer,proposal,height,round) values(" +
                block.header.number + "," +
                block.header.timestamp + ",'" +
                block.blockHash + "','" +
                block.version + "','" +
                block.header.prevHash + "','" +
                block.header.stateRoot + "','" +
                block.header.transactionsRoot + "','" +
                block.header.receiptsRoot + "','" +
                block.header.gasUsed + "','" +
                block.header.proposer + "','" +
                block.header.proof.tendermint.proposal + "','" +
                block.header.proof.tendermint.height + "','" +
                block.header.proof.tendermint.round + "'" +
                ");";
        db.execSql(sql);
    }

    public void transactionToDb(Block block) throws Exception{
        long blockNumber = block.header.number;
        Transaction[] txs = block.body.transaction;
        Transaction tx;
        String sql;
        for (int m = 0 ; m < txs.length; m ++ ) {
            tx = txs[m];
            sql = "insert into blocktransaction (number,transactionHash,transactionIndex,blockHash,cumulativeGasUsed," +
                    "gasUsed,contractAddress,root,content,toAddresss) values (" +
                    blockNumber + ",'" +
                    tx.transactionHash + "','" +
                    tx.transactionIndex + "','" +
                    tx.blockHash + "','" +
                    tx.cumulativeGasUsed + "','" +
                    tx.gasUsed + "','" +
                    tx.contractAddress + "','" +
                    tx.root + "','" +
                    tx.content + "','" +
                    tx.to + "'" +
                    ");";
            db.execSql(sql);
        }
    }

    /*
    * first start synchronization block
    * */
    public void getInitBlockData() throws Exception {

        long blocknum = chain.getBlockNumber().longValue();

        for (long i = 0; i <= blocknum; i++){
            Block block = chain.getBlockByNumber(i);
            headToDb(block);
            transactionToDb(block);
        }

    }

    /*
    * Timing synchronization block, the frequency can be same as chain block out time
    * */
    public void timeRunBlock(int period){

        new Timer("timer:"+ period ).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    long dbMaxNumber = 0;
                    String sql = "select max(number) from block ;";
                    ResultSet rs = db.execQuery(sql);
                    if (rs.next()){
                        dbMaxNumber = rs.getLong(1) + 1;
                    }
                    System.out.println("dbMaxNumber : "+ dbMaxNumber);
                    long chainNumber = chain.getBlockNumber().longValue();
                    for (long i = dbMaxNumber; i <= chainNumber; i ++ ){
                        Block block = chain.getBlockByNumber(i);
                        headToDb(block);
                        transactionToDb(block);
                        System.out.println("i : "+ i);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 0, period);
    }

    public static void main(String[] args) throws Exception {
        CitaChain citaChain = new CitaChain("http://172.16.121.150:1337" );
        Sqlite sqlite = new Sqlite("blockchain.db");
        BlockToDb blockToDb = new BlockToDb(sqlite,citaChain);
        blockToDb.createBlockChainTable();
        blockToDb.getMetaData();
        blockToDb.getInitBlockData();
        blockToDb.timeRunMetaData(10000);
        blockToDb.timeRunBlock(3000);
    }
}
