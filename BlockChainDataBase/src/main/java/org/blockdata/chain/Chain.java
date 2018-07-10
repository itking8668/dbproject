package org.blockdata.chain;

import org.blockdata.data.Block;
import org.blockdata.data.MetaData;

import java.math.BigInteger;

public interface Chain {
    public BigInteger getPeerCount() throws Exception;
    public BigInteger getBlockNumber() throws Exception;
    public MetaData getMetaData();
    public Block getBlockByNumber(long blockNumber);
}