package com.chronos.couriers;

import com.chronos.couriers.model.PackagePriorityType;
import com.chronos.couriers.service.CreatePackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ChronosCouriersApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CreatePackage createPackage = new CreatePackage();

        try {
            boolean running = true;
            while (running) {
                System.out.println("\n--- Chronos Couriers CLI ---");
                System.out.println("1. Place Order");
                System.out.println("2. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        try {
                            System.out.print("Enter Package ID: ");
                            String id = scanner.nextLine();

                            System.out.print("Enter Priority (EXPRESS/STANDARD): ");
                            PackagePriorityType priority;

                            try {
                                String input = scanner.nextLine().toUpperCase();
                                priority = PackagePriorityType.valueOf(input);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Invalid input for 'Priority'. Please enter EXPRESS or STANDARD.");
                                System.err.flush();
                                break;
                            }

                            System.out.print("Enter package volume (1-100): ");
                            int volume = Integer.parseInt(scanner.nextLine());
                            try {
                                if (volume < 1 || volume > 100) {
                                    throw new IllegalArgumentException("Volume must be between 1 and 100 inclusive.");
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid input: Volume must be an integer.");
                                System.err.flush();
                            }

                            System.out.print("Is Fragile? (Y/N): ");
                            String fragileInput = scanner.nextLine().trim().toLowerCase();

                            boolean fragile;
                            if (fragileInput.equals("y")) {
                                fragile = true;
                            } else if (fragileInput.equals("n")) {
                                fragile = false;
                            } else {
                                throw new IllegalArgumentException("Invalid input for 'Is Fragile'. Please enter Y or N.");
                            }

                            createPackage.placeOrder(id, priority, fragile, volume);

                        } catch (CreatePackage.PackageAlreadyExistsException |
                                 CreatePackage.InvalidPackageDataException e) {
                            System.err.println("Error placing package: " + e.getMessage());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid input: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Unexpected error: " + e.getMessage());
                        }
                        break;

                    case "2":
                        System.out.println("Exiting Chronos Couriers...");
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 or 2.");
                }
            }

            scanner.close();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
