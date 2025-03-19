package org.example;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Main {

    static ArrayList<Block> blockchain = new ArrayList<>();
    static int difficulty = 5;

    public static void main(String[] args) {

        blockchain.add(new Block("Hi, I am the first block.", "0"));
        System.out.println("Trying to mine block 1...");
        blockchain.get(0).mineBlock(difficulty);
//        Block genesisBlock = new Block("Hi, I am the first block.", "0");
//        System.out.println("Hash for first block: " + genesisBlock.hash);

        blockchain.add(new Block("Yo! I am the second block.", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to mine block 2...");
        blockchain.get(1).mineBlock(difficulty);
//        Block secondBlock = new Block("Yo! I am the second block.", genesisBlock.hash);
//        System.out.println("Hash for second block: " + secondBlock.hash);

        blockchain.add(new Block("Hello, I am the third block.", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Trying to mine block 3...");
        blockchain.get(2).mineBlock(difficulty);
//        Block thirdBlock = new Block("Hello, I am the third block.", secondBlock.hash);
//        System.out.println("Hash for third block: " + thirdBlock.hash);

        System.out.println("Blockchain is valid: " + isChainValid());

        String blockchainGson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainGson);
    }

    // check the integrity of our blockchain
    public static boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;

        // loop through blockchain to check hashes:
        for(int i = 1; i < blockchain.size(); i++){
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // compare registered hash and calculated hash
            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current hashes not equal.");
                return false;
            }

            // compare registered previous hash and previous hash
            if(!previousBlock.hash.equals(previousBlock.calculateHash())){
                System.out.println("Previous hashes not equal.");
                return false;
            }
        }
        return true;
    }

}