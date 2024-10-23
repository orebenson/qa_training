// package bankapp;

import java.util.HashMap;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main {
    /*
     * Main class.
     * Handles the user interaction with the application, displaying and processing the relevant menu optiobns.
     */
    Bank bank;

    public Main() {
        this.bank = new Bank();
    }

    public void runMenu() {
        /*
         * Run menu class handles taking in the user interaction options for the application.
         * The relevant methods are run depending on user input.
         * Includes some user validation.
         */
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) { // This loop will run until the user wishes to exit the application, enabling continuous interaction with the bank.
            System.out.println("\nPress enter to continue...\n");
            scanner.nextLine();
            System.out.println("\n###################################\n");
            System.out.println("Menu:");
            System.out.println("0. Load data from an existing file in the directory");
            System.out.println("1. Get all accounts");
            System.out.println("2. Get a specific account");
            System.out.println("3. Get all transactions");
            System.out.println("4. Create an account");
            System.out.println("5. Delete an account");
            System.out.println("6. Create a transaction");
            System.out.println("7. Exit");
            System.out.println("8. Run demo");
            System.out.println("\n###################################\n");

            int choice;

            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    // A switch statement is used instead of if else statements as there are only a specific set of inputs allowed.
                    // Some of the cases also contain validation, checking that the format of user inputs is correct.
                    case 0:
                        System.out.print("Enter file name: ");
                        String fileName = scanner.nextLine();
                        this.loadData(fileName);
                        break;
                    case 1:
                        this.printAccounts();
                        break;
                    case 2:
                        System.out.print("Enter account name: ");
                        String accountName = scanner.nextLine();
                        this.printAccount(accountName);
                        break;
                    case 3:
                        this.printAllTransactions();
                        break;
                    case 4:
                        System.out.print("Enter account name: ");
                        String newAccountName = scanner.nextLine();
                        System.out.print("Enter account balance: ");
                        if (scanner.hasNextDouble()) {
                            double accountBalance = scanner.nextDouble();
                            this.sendCreateAccount(newAccountName, accountBalance);
                            scanner.nextLine();
                        } else {
                            System.out.println("\nerror: Account balance must be a number (double).");
                            scanner.nextLine();
                        }
                        break;
                    case 5:
                        System.out.print("Enter account name to delete: ");
                        String accountToDelete = scanner.nextLine();
                        this.sendDeleteAccount(accountToDelete);
                        break;
                    case 6:
                        System.out.print("Enter sender name: ");
                        String sender = scanner.nextLine();
                        System.out.print("Enter receiver name: ");
                        String receiver = scanner.nextLine();
                        System.out.print("Enter amount: ");
                        if (scanner.hasNextDouble()) {
                            double amount = scanner.nextDouble();
                            this.sendCreateTransaction(sender, receiver, amount);
                            scanner.nextLine();
                        } else {
                            System.out.println("\nerror: Transaction amount must be a number (double).");
                            scanner.nextLine();
                        }
                        break;
                    case 7:
                        System.out.print("Do you want to save your data to a file? (Y/N): ");
                        String response = scanner.nextLine();
                        if(response.toLowerCase().equals("n")){
                            exit = true;
                            System.out.println("\n********************** Thank you for using Benson Bank. **********************\n");
                        } else if (response.toLowerCase().equals("y")){
                            System.out.print("Enter file name to write to: ");
                            String outputFile = scanner.nextLine();
                            this.saveData(outputFile);
                            exit = true;
                            System.out.println("\n********************** Thank you for using Benson Bank. **********************\n");
                        } else {
                            System.out.println("\nerror: Response must be 'y' or 'n'.\n");
                            scanner.nextLine();
                        }
                        break;
                    case 8:
                        this.runDemo();
                        break;
                    default:
                        System.out.println("\nerror: Invalid choice. Please select a valid option between 0 and 8.");
                        break;
                }
            } else {
                System.out.println("\nerror: Invalid choice. Please select a valid option.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private void loadData(String fileName) {
        /*
         * Loads data from an existing file into the bank, allowing for persistence.
         */
        Bank result = bank.loadData(fileName);
        if (result == null) {
            System.out.println("\nerror: Failed to load bank from file [" + fileName + "].");
        } else {
            this.bank = result;
            System.out.println("\nsuccess: Bank from file [" + fileName + "] loaded.");
        }
    }

    private void saveData(String outputFile) {
        /*
         * Saves data into a user-specified file, allowing persistence.
         */
        String result = bank.saveData(outputFile);
        System.out.println(result);
    }

    private void printAccounts() {
        /*
         * Gets and prints all user accounts and balances in the format shown below.
         */

        /*
         * #######################
         * 
         * account name | balance
         * -----------------------
         * ore          | 20.23
         * jack         | 20.23
         * ryan         | 20.23
         * tilly        | 20.23
         * peter        | 20.23
         * 
         * #######################
         */

        HashMap<String, BankAccount> accountMap = bank.getAccounts();
        if (accountMap == null) {
            System.out.println("\nerror: Bank does not contain any accounts.");
            return;
        }
        
        // Here we calculate the maximum lengths for each account name column field.
        int maxAccountNameLength = 0;
        for (String accountName : accountMap.keySet()) {
            maxAccountNameLength = Math.max(maxAccountNameLength, accountName.length());
            maxAccountNameLength = Math.max(maxAccountNameLength, "Account Name".length());
        }
        
        System.out.println("\n###################################\n");
        System.out.println("All Accounts:\n");

        // The 3 lines below use the calculated longest account name length in order to determine the width of each table column
        String headerFormat = "%-" + (maxAccountNameLength + 2) + "s |  %s%n";
        System.out.printf(headerFormat, "Account Name", "Balance");
        System.out.println("-".repeat(maxAccountNameLength + 16));

        // This for loop maps the hashmap to the table, processing it one entry at a time, printing the table line by line.
        for (HashMap.Entry<String, BankAccount> entry : accountMap.entrySet()) {
            String accountName = entry.getKey();
            BankAccount account = entry.getValue();
            double balance = account.getBalance();

            System.out.printf("%-" + (maxAccountNameLength + 2) + "s |  %.2f%n", accountName, balance);
        }
        System.out.println("\n###################################\n");

    }

    private void printAccount(String name) {
        /*
         * Gets and prints the details of the account specified by the user.
         */

        /*
         * #######################
         * 
         * account name: ore
         * account balance: 20.23
         * account transactions:
         * 
         * date     | account | amount
         * -----------------------------
         * 23/01/01 | ore     | + 20.23
         * 23/01/01 | jack    | - 20.23
         * 23/01/01 | ryan    | + 20.23
         * 23/01/01 | tilly   | + 20.23
         * 23/01/01 | peter   | - 20.23
         * 
         * #######################
         */

        BankAccount account = bank.getAccount(name.toLowerCase());
        if (account == null) {
            System.out.println("\nerror: Bank account does not exist.");
            return;
        }

        ArrayList<Transaction> transactions = account.getTransactions();

        System.out.println("\n###################################\n");

        System.out.println("Account Name: " + account.getName());
        System.out.println("Account Balance: " + account.getBalance());
        System.out.println("Account transactions: \n");
        if (transactions.size() < 1) {
            System.out.println("0 transactions for account [" + name + "].");
        } else {

            // Here we calculate the maximum lengths for each account name column field.
            int maxAccountNameLength = 0;
            for (Transaction transaction : transactions) {
                maxAccountNameLength = Math.max(maxAccountNameLength, transaction.getReceiver().getName().length());
                maxAccountNameLength = Math.max(maxAccountNameLength, transaction.getSender().getName().length());
                maxAccountNameLength = Math.max(maxAccountNameLength, "Account Name".length());
            }

            // Similarly to in the printAccounts method, these three lines use the calculated longest account name to calculate the table columns.
            String headerFormat = "%-21s | %-" + (maxAccountNameLength + 2) + "s | %s%n";
            System.out.printf(headerFormat, "Date", "Account Name", "Amount");
            System.out.println("-".repeat(43 + maxAccountNameLength));

            for (Transaction transaction : transactions) {
                double doubleAmount = transaction.getAmount();
                float amount = (float) doubleAmount;
                LocalDateTime time = transaction.getTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedTime = time.format(formatter);

                // This if statement formats the table relative to the user, checking whether the user is the sender or receiver for each transaction, printing row by row in the table.
                if (account == transaction.getSender()) {
                    String accountName = transaction.getReceiver().getName();
                    String newAmount = "- " + amount;
                    System.out.printf("%-21s | %-" + (maxAccountNameLength + 2) + "s | %s%n", formattedTime,
                            accountName,
                            newAmount);
                } else if (account == transaction.getReceiver()) {
                    String accountName = transaction.getSender().getName();
                    String newAmount = "+ " + amount;
                    System.out.printf("%-21s | %-" + (maxAccountNameLength + 2) + "s | %s%n", formattedTime,
                            accountName,
                            newAmount);
                }
            }
        }

        System.out.println("\n###################################\n");

    }

    private void printAllTransactions() {
        /*
         * Gets all transactions from the bank and returns the details of each, in the format shown below.
         * Includes validation for the bank.
         */

        /*
         * ######################################
         * 
         * date     | sender | receiver | amount
         * --------------------------------------
         * 12/10/24 | ore    | jack     | 20.23
         * 12/10/24 | jack   | tilly    | 13.16
         * 12/10/24 | ryan   | tilly    | 135.13
         * 12/10/24 | tilly  | peter    | 1313.51
         * 12/10/24 | peter  | ore      | 631.13
         * 
         * ######################################
         */
        ArrayList<Transaction> transactions = bank.getAllTransactions();
        if (transactions == null) {
            System.out.println("\nerror: Bank does not contain any transactions.");
            return;
        }

        System.out.println("\n###################################\n");
        System.out.println("All Transactions: \n");

        int maxReceiverNameLength = 0;
        int maxSenderNameLength = 0;
        for (Transaction transaction : transactions) {
            maxReceiverNameLength = Math.max(maxReceiverNameLength, transaction.getReceiver().getName().length());
            maxReceiverNameLength = Math.max(maxReceiverNameLength, "Receiver".length());
            maxSenderNameLength = Math.max(maxSenderNameLength, transaction.getSender().getName().length());
            maxSenderNameLength = Math.max(maxSenderNameLength, "Sender".length());
        }

        // These three lines use the calculated max width of the sender and receiver columns in the table
        String headerFormat = "%-21s | %-" + (maxSenderNameLength + 2) + "s | %-" + (maxReceiverNameLength + 2) + "s | %s%n";
        System.out.printf(headerFormat, "Date", "Sender", "Receiver", "Amount");
        System.out.println("-".repeat(43 + maxReceiverNameLength + maxSenderNameLength));

        // This for loop prints the table line by line, applying the appropriate formatting where relevant.
        for (Transaction transaction : transactions) {
            double doubleAmount = transaction.getAmount();
            float amount = (float) doubleAmount;
            LocalDateTime time = transaction.getTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedTime = time.format(formatter);
            String senderName = transaction.getSender().getName();
            String receiverName = transaction.getReceiver().getName();
            System.out.printf(
                    "%-21s | %-" + (maxSenderNameLength + 2) + "s | %-" + (maxReceiverNameLength + 2) + "s | %.2f%n",
                    formattedTime, senderName, receiverName, amount);
        }

        System.out.println("\n###################################\n");

    }

    private void sendCreateAccount(String name, double balance) {
        /*
         * Creates an account in the current bank, and prints the result from the bank class.
         */
        String result = bank.createAccount(name.toLowerCase(), Math.floor(balance*100)/100);
        System.out.println(result);
    }

    private void sendDeleteAccount(String name) {
        /*
         * Deletes an account from the current bank, and prints the result from the bank class.
         */
        String result = bank.deleteAccount(name.toLowerCase());
        System.out.println(result);
    }

    private void sendCreateTransaction(String sender, String receiver, double amount) {
        /*
         * Creates a transaction between two users, and prints the result from the bank class.
         */
        String result = bank.createTransaction(sender.toLowerCase(), receiver.toLowerCase(), Math.floor(amount*100)/100);
        System.out.println(result);
    }
    
    private void runDemo() {
        /*
         * Runs demo script, which creates users and transactions for the user to test with.
         */
        System.out.println("\nRunning demo...\n");

        this.sendCreateAccount("Ore", 100000.46);
        this.sendCreateAccount("Tom", 3080.36);
        this.sendCreateAccount("Jimmy", 5000.82);
        this.sendCreateAccount("Rachel", 2300.92);
        this.sendCreateAccount("Bob", 3500.55);

        this.sendCreateTransaction("Ore", "Tom", 50.64);
        this.sendCreateTransaction("Ore", "Jimmy", 120.24);
        this.sendCreateTransaction("Jimmy", "Rachel", 1250.32);
        this.sendCreateTransaction("Jimmy", "Ore", 85.83);
        this.sendCreateTransaction("Jimmy", "Bob", 390.77);
        this.sendCreateTransaction("Rachel", "Tom", 1500.99);
        this.sendCreateTransaction("Rachel", "Jimmy", 94.85);
        this.sendCreateTransaction("Rachel", "Tom", 100.65);
        this.sendCreateTransaction("Bob", "Ore", 265.77);
        this.sendCreateTransaction("Bob", "Rachel", 125.54);
        this.sendCreateTransaction("Tom", "Bob", 1350.87);
        this.sendCreateTransaction("Tom", "Jimmy", 90.62);

        this.saveData("demo.tmp");

        System.out.println("\nDemo accounts and transactions loaded and saved to file 'demo.tmp'.\n");
    }

    public static void main(String[] args) {
        /*
         * Entry point of the application.
         * Initialises the application and runs the menu method.
         */
        Main main = new Main();
        System.out.println("\n********************** Welcome to Benson Bank. **********************\n");
        main.runMenu();
    }
}
