package org.example;

import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static ArrayList<Block> blockchain = new ArrayList<>();

    // list of all unspent transactions that can be used as inputs
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;

    public static Transaction genesisTransaction;
    public static float minimumTransaction = 0.1f;


    public static void main(String[] args) {
        //Setup bouncyCastle as a Security Provider
        Security.addProvider(new BouncyCastleProvider());

        // Create the new wallets
        walletA = new Wallet();
        walletB = new Wallet();

        Wallet coinbase = new Wallet();

        // create gebesis transaction, which sends 100 NoobCoins to walletA
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey); // manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; // manually set the transactionId
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); // manually add the transaction output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); // its important to store our first transaction in the UTXOs list

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransactions(genesisTransaction);
        addBlock(genesis);


        // testing
        Block block1 = new Block(genesis.hash);
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletA is attempting to send funds (40) to WalletB...");
        block1.addTransactions(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("WalletA is attempting to send more fudnds (1000) that it has...");
        block2.addTransactions(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("WalletB is attempting to send funds (20) to WalletA");
        block3.addTransactions(walletB.sendFunds(walletA.publicKey, 20));
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();

        // test public and private keys
//        System.out.println("Public and Private keys: ");
//        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
//        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));

        // create a test transaction from walletA to walletB
//        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
//        transaction.generateSignature(walletA.privateKey);
//
//        // verify the signature works and verify it from the public key
//        System.out.println("Is signature verified? " + transaction.verifySignature());

//        blockchain.add(new Block("Hi, I am the first block.", "0"));
//        System.out.println("Trying to mine block 1...");
//        blockchain.get(0).mineBlock(difficulty);
////        Block genesisBlock = new Block("Hi, I am the first block.", "0");
////        System.out.println("Hash for first block: " + genesisBlock.hash);
//
//        blockchain.add(new Block("Yo! I am the second block.", blockchain.get(blockchain.size() - 1).hash));
//        System.out.println("Trying to mine block 2...");
//        blockchain.get(1).mineBlock(difficulty);
////        Block secondBlock = new Block("Yo! I am the second block.", genesisBlock.hash);
////        System.out.println("Hash for second block: " + secondBlock.hash);
//
//        blockchain.add(new Block("Hello, I am the third block.", blockchain.get(blockchain.size()-1).hash));
//        System.out.println("Trying to mine block 3...");
//        blockchain.get(2).mineBlock(difficulty);
////        Block thirdBlock = new Block("Hello, I am the third block.", secondBlock.hash);
////        System.out.println("Hash for third block: " + thirdBlock.hash);
//
//        System.out.println("Blockchain is valid: " + isChainValid());
//
//        String blockchainGson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//        System.out.println(blockchainGson);
    }

    // check the integrity of our blockchain
    public static boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>(); // a temporary working list of unspent transactions at a given state
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

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

            // check if hash is solved
            if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)){
                System.out.println("# This block hasn't been mined.");
                return false;
            }

            // loop through blockchain transactions
            TransactionOutput tempOutput;
            for(int t = 0; t < currentBlock.transactions.size(); t++){
                Transaction currentTransaction = currentBlock.transactions.get(t);

                // verify signature
                if(!currentTransaction.verifySignature()){
                    System.out.println("# Signature on transaction " + t + " is Invalid.");
                    return false;
                }

                // check if inputs are not equal to the outputs
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()){
                    System.out.println("# Inputs are not equal to the outputs on Transaction " + t);
                    return false;
                }

                for(TransactionInput input : currentTransaction.inputs){
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null){
                        System.out.println("# Referenced input on Transaction " + t + " is Missing!");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value){
                        System.out.println("# Referenced input transaction " + t + " is Invalid!");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output : currentTransaction.outputs){
                    tempUTXOs.put(output.id, output);
                }

                if(currentTransaction.outputs.get(0).recipient != currentTransaction.recipient){
                    System.out.println("# Transaction " + t + " output recipient is not who it should be");
                    return false;
                }

                if(currentTransaction.outputs.get(1).recipient != currentTransaction.sender){
                    System.out.println("# Transaction " + t + " output 'change' is not sender." );
                    return false;
                }

            }
        }
        System.out.println("Blockchain is valid!");
        return true;
    }

    public static void addBlock(Block newBlock){
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

}