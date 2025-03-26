package org.example;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();

    Block(String data, String previousHash){
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    Block(String previousHash){
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash(){
        String calculatedHash = previousHash + data + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot;
        return StringUtil.applysha256(calculatedHash);
    }

    // Let's start mining blocks (Proof of Work)
    // We will require miners to do proof-of-work by trying different variable values in the block until its hash starts with a certain number of 0’s.
    public void mineBlock(int difficulty){
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target_hash = new String(new char[difficulty]).replace('\0', '0');

        // keep generating new hash values by modifying the nonce value until it satisfy the condition (until the hash starts with difficulty number of zeroes)
        while(!hash.substring(0, difficulty).equals(target_hash)){
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined! " + hash);
    }

    // add transactions to this block
    public boolean addTransactions(Transaction transaction){
        // process transaction and check if its valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if(previousHash != "0"){
            if(!transaction.processTransaction()){
                System.out.println("# Transaction failed to process. Discarded!!");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("# Transaction successfully added to Block.");
        return true; // returns true only if the transaction has been successfully added.
    }
}
