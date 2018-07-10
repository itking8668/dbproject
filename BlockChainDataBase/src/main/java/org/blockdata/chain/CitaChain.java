package org.blockdata.chain;
import org.blockdata.data.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.TransactionUtil;

import java.math.BigInteger;
import java.util.List;

public class CitaChain implements Chain {
    private Web3j web3;
    private MetaData metaData;
    public CitaChain(String url) throws Exception{
        web3 = Web3j.build(new HttpService(url));
    }

    public BigInteger getPeerCount() throws Exception{
            return web3.netPeerCount().send().getQuantity();
    }


    public BigInteger getBlockNumber() throws Exception{
            return web3.ethBlockNumber().send().getBlockNumber();
    }

    public MetaData getMetaData(){
        try {
            EthMetaData.EthMetaDataResult md = web3.ethMetaData(DefaultBlockParameter.valueOf("latest")).send().getEthMetaDataResult();
            int validatorsLength = md.validators.length;
            if (metaData == null) {
                metaData = new MetaData();
                metaData.chainId = md.chainId;
                metaData.blockInterval = md.blockInterval;
                metaData.genesisTimestamp = md.genesisTimestamp;
                metaData.chainName = md.chainName;
                metaData.operator = md.operator;
                metaData.website = md.website;
            }
            metaData.validators = new String[validatorsLength];
            for (int i = 0; i < validatorsLength; i++) {
                metaData.validators[i] = md.validators[i].getValue();
            }
            return metaData;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Block getBlockByNumber(long blockNumber) {
        try {
            Block block = new Block();
            block.header = new Head();
            block.header.proof = new Proof();
            block.header.proof.tendermint = new Tendermint();
            block.body = new Body();

            EthBlock.Block sourceBlock = web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true).send().getBlock();
            block.blockHash = sourceBlock.getHash();
            block.version = sourceBlock.getVersion();

            EthBlock.Header header = sourceBlock.getHeader();
            block.header.timestamp = header.getTimestamp();
            block.header.number = blockNumber;
            block.header.prevHash = header.getPrevHash();
            block.header.stateRoot = header.getStateRoot();
            block.header.transactionsRoot = header.getTransactionsRoot();
            block.header.receiptsRoot = header.getReceiptsRoot();
            block.header.gasUsed = header.getGasUsed();

//            block.header.proposer =

//            EthBlock.Proof sourceProof = header.getProof();
//            block.header.proof.proposer = sourceProof.getProposal();
//            block.header.proof.tendermint.height = sourceProof.getHeight();
//            block.header.proof.tendermint.round = sourceProof.getRound();
//            block.header.proof.tendermint.proposal  = sourceProof.getProposal();

            EthBlock.Body body = sourceBlock.getBody();
            List<EthBlock.TransactionResult> transactionResults = body.getTransactions();
            int transactionLength = transactionResults.size();
            block.body.transaction = new org.blockdata.data.Transaction[transactionLength];
            for (int i = 0 ; i < transactionLength; i ++ ){
                EthBlock.TransactionResult transactionResult = transactionResults.get(i);
                Transaction sourceTransaction = ((EthBlock.TransactionObject)transactionResult).get();
                org.blockdata.data.Transaction transaction = new org.blockdata.data.Transaction();
                transaction.blockHash = block.blockHash;
                transaction.transactionHash = sourceTransaction.getHash();
                transaction.content = sourceTransaction.getContent();
                TransactionReceipt tx = web3.ethGetTransactionReceipt(transaction.transactionHash).send().getTransactionReceipt().get();
                transaction.transactionIndex = tx.getTransactionIndexRaw();
                transaction.gasUsed = tx.getGasUsedRaw();
                transaction.contractAddress = tx.getContractAddress();
                transaction.cumulativeGasUsed = tx.getCumulativeGasUsedRaw();
                transaction.to = tx.getTo();
                block.body.transaction[i] = transaction;

            }
            return block;
        }catch (Exception e){
            System.out.println("get block by number error ,block number :"+ blockNumber);
            e.printStackTrace();
            return null;
        }
    }

    public void dumpCitaMetaData(MetaData metaData){
        System.out.println("chainId : " + metaData.chainId);
        System.out.println("blockInterval : " + metaData.blockInterval);
        System.out.println("genesisTimestam : " + metaData.genesisTimestamp);
        System.out.println("chainName : " + metaData.chainName);
        System.out.println("operator : " + metaData.operator);
        System.out.println("website : " + metaData.website);
        System.out.println("tokenName : " + metaData.tokenName);
        System.out.println("tokenSymbol : " + metaData.tokenSymbol);
        System.out.println("tokenAvatar : " + metaData.tokenAvatar);

        for (int i = 0; i < metaData.validators.length ; i++) {
            System.out.println("validators [" + i + "] : " + metaData.validators[i]);
        }
    }

    public void dumpCitaBlockHead(EthBlock.Block block){
        System.out.println("version : " + block.getVersion());
        System.out.println("hash : " + block.getHash());
        System.out.println("------header-----------");

        EthBlock.Header header = block.getHeader();

        System.out.println("timestamp: " + header.getTimestamp());
        System.out.println("prevHash : " + header.getPrevHash());
        System.out.println("number : " + header.getNumber());
        System.out.println("stateRoot : " + header.getStateRoot());
        System.out.println("transactionsRoot : " + header.getTransactionsRoot());
        System.out.println("receiptsRoot : " + header.getReceiptsRoot());
        System.out.println("gasUsed : " + header.getGasUsed());
        System.out.println("gasUsedDec : " + header.getGasUsedDec());

//        System.out.println("-------poof----------");
//
//        EthBlock.Proof proof = header.getProof();
//        System.out.println("proposal : " + proof.getProposal());
//        System.out.println("height : " + proof.getHeight());
//        System.out.println("round : " + proof.getRound());
    }
    public void dumpCitaBlockBody(Transaction transaction) {
        System.out.println("TxHash : " + transaction.getHash());
        System.out.println("content : " + transaction.getContent());

        org.web3j.protobuf.Blockchain.Transaction tx = TransactionUtil.getTransaction(transaction.getContent());
        System.out.println("getData: " + tx.getData().toString());
        System.out.println("getQuota " + tx.getQuota());
        System.out.println("getNonce " + tx.getNonce());
        System.out.println("getTo " + tx.getTo());
        System.out.println("getValidUntilBlock " + tx.getValidUntilBlock());
        System.out.println("getValue " + tx.getValue().toString());
        System.out.println("getVersion " + tx.getVersion());
        System.out.println("getSerializedSize " + tx.getSerializedSize());
//        Transaction tx = web3.ethGetTransactionByHash(transaction.getHash()).send().getTransaction().get();

//        System.out.println("getBlockHash " + tx.getBlockHash());
//        System.out.println("getBlockNumber " + tx.getBlockNumber());
//        System.out.println("getContent " + tx.getContent());
//        System.out.println("getFrom " + tx.getFrom());
//        System.out.println("getTo " + tx.getTo());
//        System.out.println("getGas " + tx.getGas());
//        System.out.println("getGasPrice " + tx.getGasPrice());
//        System.out.println("getHash " + tx.getHash());
//        System.out.println("getIndex " + tx.getIndex());
//        System.out.println("getPublicKey " + tx.getPublicKey());
//        System.out.println("getValue " + tx.getValue());
//        System.out.println("getTransactionIndex " + tx.getTransactionIndex());
//        System.out.println("getNonce " + tx.getNonce());
    }

    public void dumpBlockHead(Block block){
        System.out.println("------header-----------");
        System.out.println("version : " + block.version);
        System.out.println("hash : " + block.blockHash);

        Head header = block.header;

        System.out.println("timestamp: " + header.timestamp);
        System.out.println("prevHash : " + header.prevHash);
        System.out.println("number : " + header.number);
        System.out.println("stateRoot : " + header.stateRoot);
        System.out.println("transactionsRoot : " + header.transactionsRoot);
        System.out.println("receiptsRoot : " + header.receiptsRoot);
        System.out.println("gasUsed : " + header.gasUsed);

//        System.out.println("-------poof----------");

//        Proof proof = header.proof;
//        System.out.println("proposal : " + proof.tendermint.proposal);
//        System.out.println("proposer : " + proof.proposer);
//        System.out.println("height : " + proof.tendermint.height);
//        System.out.println("round : " + proof.tendermint.round);
    }
    public void dumpBlockBody(org.blockdata.data.Transaction transaction) {
        System.out.println("--------Body------------");
        if(transaction == null){
            return;
        }
        System.out.println("TxHash : " + transaction.transactionHash);
        System.out.println("transactionIndex : " + transaction.transactionIndex);
        System.out.println("blockHash : " + transaction.blockHash);
        System.out.println("cumulativeGasUsed : " + transaction.cumulativeGasUsed);
        System.out.println("gasUsed : " + transaction.gasUsed);
        System.out.println("contractAddress : " + transaction.contractAddress);
        System.out.println("root : " + transaction.root);
        System.out.println("content : " + transaction.content);
        System.out.println("getTo : " + transaction.to);
    }


    public static void main(String[] args) throws Exception {
        CitaChain cita = new CitaChain("http://172.16.121.150:1337");
        BigInteger blockNumber = cita.getBlockNumber();
        System.out.println("Block Number : " + blockNumber );
        System.out.println("peerCount : " + cita.getPeerCount());
        System.out.println("-------MetaData-------------");
        cita.dumpCitaMetaData(cita.getMetaData());
        int i = 18101;
        for ( ; i < 18112 ; i++) {
            Block block = cita.getBlockByNumber(i);
            cita.dumpBlockHead(block);
            for (int j = 0 ; j < block.body.transaction.length; j++) {
                cita.dumpBlockBody(block.body.transaction[j]);
            }
        }
        System.out.println("finish");
    }
}