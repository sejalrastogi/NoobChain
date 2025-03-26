package org.example;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    Wallet(){
        generateKeyPair();
    }

    public void generateKeyPair(){
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize a KeyGenerator and generate a key pair
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // returns balance and stores the UTXO's owned by this wallet in the this.UTXO
    public float getBalance(){
        float total = 0;
        for(Map.Entry<String, TransactionOutput> entry : Main.UTXOs.entrySet()){
            TransactionOutput UTXO = entry.getValue();
            if(UTXO.isMine(publicKey)){ // if the output belongs to me (if coin belongs to me)
                UTXOs.put(UTXO.id, UTXO); // then add it to our list of unspent transaction
                total += UTXO.value;
            }
        }
        return total;
    }

    // generates and returns a new transaction from this wallet
    public Transaction sendFunds(PublicKey recipient, float value){
        // gather and check if there's enough funds to send
        if(getBalance() < value){
            System.out.println("Not enough funds to send transaction. Transaction Discarded!");
            return null;
        }

        // create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for(Map.Entry<String, TransactionOutput> entry : UTXOs.entrySet()){
            TransactionOutput UTXO = entry.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input : inputs){
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }
}
