package org.example;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; // this is also the hash of the transaction
    public PublicKey sender; // sender's address / public key
    public PublicKey recipient; // recipient's address / public key
    public float value;
    public byte[] signature; // this is to prevent anybody else from spending funds in our wallet

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    public int sequence = 0; // a rough count of how many transactions have been generated

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // Calculates the transaction hash (which will be used as its ID)
    public String calculateHash(){
        sequence++; // increases the sequence to avoid two identical transactions having the same hash
        return StringUtil.applysha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value) + Integer.toString(sequence));
    }

    // CreateSignature(PrivateKey, FROM, TO, VALUE)
    // Signs with all the data we don't want to be tampered with
    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // VerifySignature(PublicKey, Signature, FROM, TO, VALUE)
    // Verifies the data we signed, hasn't been tampered with
    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    // perform some checks to ensure that the transaction is valid
    // gather inputs
    // generate outputs
    // discard inputs from our list of UTXOs (a transaction output can be only be used once - as an input)
    // returns true if new transaction could be created
    public boolean processTransaction(){

        // verify signature
        if(!verifySignature()){
            System.out.println("# Transaction Signature failed to verify!");
            return false;
        }

        // gather transaction inputs (make sure they are unspent)
        for(TransactionInput i : inputs){
            i.UTXO = Main.UTXOs.get(i.transactionOutputId);
        }

        // check if transaction is valid
        if(getInputsValue() <  Main.minimumTransaction){
            System.out.println("# Transaction inputs too small: " + getInputsValue());
            return false;
        }

        // generate transaction outputs:
        float leftOver = getInputsValue() - value; // calculate left over change
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // sender sends the change back to themselves

        // add outputs to unspent list
        for(TransactionOutput o : outputs){
            Main.UTXOs.put(o.id, o);
        }

        // remove transaction inputs from UTXO list as we spent it
        for(TransactionInput i : inputs){
            if(i.UTXO == null) continue; // if transaction can't be found, skip it
            Main.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue(){ // returns sum of inputs(UTXOs) value
        float total = 0;
        for(TransactionInput i : inputs){
            if(i.UTXO == null) continue; // if transaction can't be found, skip it
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue(){ // returns sum of outputs
        float total = 0;
        for(TransactionOutput o : outputs){
            total += o.value;
        }
        return total;
    }


}
