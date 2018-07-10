package org.blockdata.data;

public class Head{
    public long timestamp;
    public long number;
    public String prevHash;
    public String stateRoot;
    public String transactionsRoot;
    public String receiptsRoot;
    public String gasUsed;
    public String proposer;
    public Proof proof;
}