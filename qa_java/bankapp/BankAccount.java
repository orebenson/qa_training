// package bankapp;

import java.io.Serializable;
import java.util.ArrayList;

public class BankAccount implements Serializable {
    /*
     * Bank account class.
     * Implements serializable in order to enable reading and writing contents to a file.
     * Contains all the fields and methods for interacting with a specific account.
     */
    private String name;
    private double balance;
    private ArrayList<Transaction> transactions;
    private boolean deleted;

    public BankAccount(String name, double balance){
        /*
         * Contructor for bank account, initializes name and balance, and creates new account.
         */
        this.name = name;
        this.balance = balance;
        this.transactions = new ArrayList<Transaction>();
        this.deleted = false;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        /*
         * Subtracts/adds the amount in the transaction, depending on whether the account is the sender or receiver.
         * Adds the transaction to this accounts list of related transactions.
         */
        double amount = transaction.getAmount();
        if(this == transaction.getSender()) {
            this.balance -= amount;
        } else {
            this.balance += amount;
        }
        this.transactions.add(transaction);
    }

    public void setDeleted() {
        /*
         * Sets the bank account to 'deleted'.
         * Adds the deleted tag to be used when the transaction list is called.
         */
        if(!deleted){
            this.name = name + " (deleted)";
            this.deleted = true;
        }
    }
    
}
