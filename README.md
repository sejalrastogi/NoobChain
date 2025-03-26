# NoobChain - A Simple Blockchain Implementation in Java

## Overview

NoobChain is a basic blockchain implementation in Java that demonstrates core blockchain concepts such as block mining, transactions, and digital wallets. It uses cryptographic signatures for secure transactions and a proof-of-work consensus mechanism for block validation.

## Features

- Implementation of a blockchain with mining difficulty

- Transaction verification using digital signatures

- A simple wallet system for sending and receiving funds

- Genesis block creation with initial NoobCoins

- Proof-of-Work mechanism for mining new blocks

- Validation of blockchain integrity

## Prerequisites

**Ensure you have the following installed:**

- Java Development Kit (JDK) 8 or later

- Maven (optional, for dependency management)

- BouncyCastle Security Provider for cryptographic functions

- Gson for JSON serialization

## Usage

### Creating and Mining the Genesis Block

The genesis block is the first block in the blockchain and assigns an initial balance to WalletA.

```
System.out.println("Creating and Mining Genesis block...");
Block genesis = new Block("0");
genesis.addTransactions(genesisTransaction);
addBlock(genesis);
```

### Conducting Transactions

WalletA sends 40 NoobCoins to WalletB:

```
System.out.println("WalletA is attempting to send funds (40) to WalletB...");
block1.addTransactions(walletA.sendFunds(walletB.publicKey, 40f));
addBlock(block1);
```

### Blockchain Validation

The integrity of the blockchain is verified through hash comparisons and digital signatures.

```
public static boolean isChainValid(){
    // Validation logic ensuring hashes, signatures, and transactions are correct
}
```

## Code Structure

- Main.java: Entry point of the program, manages blockchain operations.

- Block.java: Defines a block, containing transactions and proof-of-work.

- Transaction.java: Handles transactions between wallets.

- Wallet.java: Manages key pairs and fund transfers.

- TransactionOutput.java: Stores unspent transactions (UTXOs).

- TransactionInput.java: References previous transactions for verification.

## Blockchain Security

This project demonstrates basic blockchain security mechanisms such as:

- **Cryptographic Hashing:** SHA-256 is used to hash blocks.

- **Digital Signatures:** Transactions are signed using private keys and verified using public keys.

- **Proof-of-Work (PoW):** Mining requires solving a cryptographic puzzle.

