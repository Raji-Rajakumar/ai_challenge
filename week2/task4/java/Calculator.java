import java.util.Scanner;

public class Calculator {
    public static double add(double x, double y) {
        return x + y;
    }

    public static double subtract(double x, double y) {
        return x - y;
    }

    public static double multiply(double x, double y) {
        return x * y;
    }

    public static String divide(double x, double y) {
        if (y == 0) {
            return "Error: Cannot divide by zero";
        }
        return String.valueOf(x / y);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Simple Calculator");
        System.out.println("1. Add");
        System.out.println("2. Subtract");
        System.out.println("3. Multiply");
        System.out.println("4. Divide");
        
        while (true) {
            System.out.print("Enter choice (1/2/3/4) or 'q' to quit: ");
            String choice = scanner.nextLine();
            
            if (choice.toLowerCase().equals("q")) {
                System.out.println("Goodbye!");
                break;
            }
            
            if (!choice.matches("[1-4]")) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }
            
            System.out.print("Enter first number: ");
            double num1 = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Enter second number: ");
            double num2 = Double.parseDouble(scanner.nextLine());
            
            switch (choice) {
                case "1":
                    System.out.printf("%.2f + %.2f = %.2f%n", num1, num2, add(num1, num2));
                    break;
                case "2":
                    System.out.printf("%.2f - %.2f = %.2f%n", num1, num2, subtract(num1, num2));
                    break;
                case "3":
                    System.out.printf("%.2f * %.2f = %.2f%n", num1, num2, multiply(num1, num2));
                    break;
                case "4":
                    String result = divide(num1, num2);
                    System.out.printf("%.2f / %.2f = %s%n", num1, num2, result);
                    break;
            }
            
            System.out.println();
        }
        
        scanner.close();
    }
} 