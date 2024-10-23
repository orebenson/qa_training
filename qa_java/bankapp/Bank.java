// package bankapp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Bank implements Serializable {
    /*
     * Bank class. 
     * Stores the accounts and transactions.
     * Implements serializable in order to enable writing and reading from a file.
     */

    private HashMap<String, BankAccount> accounts;
    private ArrayList<Transaction> transactions;

    public Bank() {
        /*
         * Constructor for the Bank. Creates empty hashmap to store all acounts, and empty arraylist to store all transactions.
         */
        this.accounts = new HashMap<String, BankAccount>();
        this.transactions = new ArrayList<Transaction>();
    }

    public Bank loadData(String fileName) {
        /*
         * Data loader for the bank.
         * De-serializes the local file, and returns the contents (returns a Bank object).
         * I am using a try catch statement to catch any errors that may occur when using the ObjectInputStream , and casting the input to a Bank object.
         */
        Bank loadedBank = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            loadedBank = (Bank)ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedBank;
    }

    public String saveData(String fileName) {
        /*
         * Dater saver for the bank.
         * Serializes Bank object, writing it and its contents to a file with the users' given file name.
         * I am using a try catch statement to catch any errors when writing the file.
         */
        String message = "\nerror: Data could not be saved to file [" + fileName + "]."; 
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
            oos.flush();
            oos.close();
            message = "\nsuccess: Data saved successfully to file [" + fileName + "].";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public HashMap<String, BankAccount> getAccounts() {
        /*
         * Returns hashmap of name:BankAccount pairs.
         */
        if (accounts.size() < 1) {
            return null;
        }
        return accounts;
    }

    public BankAccount getAccount(String name) {
        /*
         * Returns bank account object with given name, if it exists.
         */
        if (!accounts.containsKey(name)) {
            return null;
        }
        return accounts.get(name);
    }

    public ArrayList<Transaction> getAllTransactions() {
        /*
         * Returns arraylist of all transactions currently in the bank.
         */
        if (transactions.size() == 0) {
            return null;
        }
        return transactions;
    }

    public String createAccount(String name, double balance) {
        /*
         * Creates a new bank account with given name and balance.
         * It then checks if the name and balance are valid, then creates new account and adds it to the hashmap of accounts.
         * Returns the error/success message.
         */
        String message = "";
        if (accounts.containsKey(name)) {
            message = "\nerror: Account with name [" + name + "] already exists.";
        } else if (balance < 0) {
            message = "\nerror: Balance must be more than more equal to 0.";
        } else {
            this.accounts.putIfAbsent(name, new BankAccount(name, balance));
            message = "\nsuccess: New account with name [" + name + "]" + " and balance [" + balance + "] created.";
        }
        return message;
    }

    public String deleteAccount(String name) {
        /*
         * Deletes the account with the given name if it exists.
         * Adds (deleted) to the name in the transactions as any transactions to that account will still need to be viewed.
         * Returns relevant error/success message.
         */
        String message = "";
        if (accounts.containsKey(name)) {
            for(Transaction transaction : transactions) {
                BankAccount sender = transaction.getSender();
                BankAccount receiver = transaction.getReceiver();
                if(sender.getName().equals(name)){sender.setDeleted();}
                if(receiver.getName().equals(name)){receiver.setDeleted();}
            }
            accounts.remove(name);
            message = "\nsuccess: Account with name [" + name + "] deleted.";
        } else {
            message = "\nerror: Account with name [" + name + "] does not exist.";
        }
        return message;
    }

    public String createTransaction(String sender, String receiver, double amount) {
        /*
         * Creates a new transaction between two given users if they both exist and the sender has sufficient funds.
         * Returns the relevant error/success message.
         */
        String message = "";
        if (!accounts.containsKey(sender)) {
            message = "\nerror: Sender [" + sender + "] does not exist.";
        } else if (!accounts.containsKey(receiver)) {
            message = "\nerror: Receiver [" + receiver + "] does not exist.";
        } else if (accounts.get(sender).getBalance() < amount) {
            message = "\nerror: Sender [" + sender + "] does not have sufficient funds.";
        } else if (amount < 0) {
            message = "\nerror: Amount must be more than or equal to 0.";
        } else {
            BankAccount senderAccount = accounts.get(sender);
            BankAccount receiverAccount = accounts.get(receiver);
            Transaction transaction = new Transaction(senderAccount, receiverAccount, amount);
            this.transactions.add(transaction);
            senderAccount.addTransaction(transaction);
            receiverAccount.addTransaction(transaction);
            message = "\nsuccess: Transaction of amount [" + amount + "] sent from account [" + sender + "] to account [" + receiver + "].";
        }
        return message;
    }
}