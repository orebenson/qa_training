// package bankapp;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    /*
     * Transaction class.
     * Stores the sender, receiver, amount, and time for each transaction.
     * This class is necessary so that each transaction is encapsulated separately from the users.
     */
    private BankAccount sender;
    private BankAccount receiver;
    private double amount;
    private LocalDateTime time;

    public Transaction(BankAccount sender, BankAccount receiver, double amount) {
        /*
         * Transaction constructor.
         * Sets the fields passed as parameters, and sets the time to the time which it is created.
         */
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    public BankAccount getSender() {
        return sender;
    }

    public BankAccount getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
