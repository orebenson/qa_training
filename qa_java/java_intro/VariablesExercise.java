import java.util.Scanner;

public class VariablesExercise {
    public static void main(String[] args) {
        // // Task 1
        String firstName = "John";
        String lastName = "Cena";
        String name = firstName + ' ' + lastName;
        System.out.println(name);


        // // Task 2
        int num1 = 2;
        int num2 = 4;

        System.out.println(num1 + num2);

        double result = num1 / num2;
        int num3 = 5;

        System.out.println(num3++); // prints num3 then adds 1
        System.out.println(num3); // prints the result of num3 + 1 from previous line
        System.out.println(++num3); // adds 1 to num3 then prints

        // Task 3
        Scanner sc = new Scanner(System.in);

        System.out.println("\nWhat is your first name? ");
        String userFirstName = sc.nextLine();
        System.out.println("\nWhat is your last name? ");
        String userLastName = sc.nextLine();
        System.out.println("\nGood morning, " + userFirstName + ' ' + userLastName);


        System.out.println("\nWhat is the first number? ");
        double userFirstNumber = sc.nextDouble();
        System.out.println("\nWhat is the second number? ");
        double userSecondNumber = sc.nextDouble();
        System.out.println("\nThe sum of these numbers is: " + (userFirstNumber + userSecondNumber));

    }
}



