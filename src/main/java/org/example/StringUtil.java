package org.example;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtil {

    // Applies Sha256 to a string and returns the result.
    public static String applysha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Applies ECDSA Signature and returns the result
    public static byte[] applyECDSASig(PrivateKey privateKey, String input){
        byte[] output = new byte[0];
        Signature dsa;

        try{
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    // Verifies a String Signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature){
        try{
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // takes in array of transactions and returns a merkle root
    public static String getMerkleRoot(ArrayList<Transaction> transactions){
        int count = transactions.size();

        ArrayList<String> previousTreeLayer = new ArrayList<>();
        for(Transaction transaction : transactions){
            previousTreeLayer.add(transaction.transactionId);
        }

        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1){
            treeLayer = new ArrayList<>();
            for(int i = 1; i < previousTreeLayer.size(); i++){
                treeLayer.add(applysha256(previousTreeLayer.get(i - 1)) + applysha256(previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
