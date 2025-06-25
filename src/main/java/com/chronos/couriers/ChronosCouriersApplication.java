package com.chronos.couriers;

import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;
import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import com.chronos.couriers.service.packageservice.CreatePackage;
import com.chronos.couriers.service.packageservice.PackagePriority;
import com.chronos.couriers.service.riderservice.AssignPackageToRider;
import com.chronos.couriers.service.riderservice.CreateRider;
import com.chronos.couriers.service.riderservice.RidersAndAssignedPackages;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.chronos.couriers.model.packageinfo.Package;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.Scanner;

@SpringBootApplication
public class ChronosCouriersApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PackagePriority packagePriority = new PackagePriority();
        CreatePackage createPackage = new CreatePackage(packagePriority);
        CreateRider createRider = new CreateRider();

        try {
            boolean running = true;
            while (running) {
                System.out.println("\n--- Chronos Couriers CLI ---");
                System.out.println("1. Create Rider");
                System.out.println("2. Create Order");
                System.out.println("3. View All Packages");
                System.out.println("4. Assign Packages to Riders");
                System.out.println("5. View Packages By RiderId");
                System.out.println("6. Update Package Delivery Status");
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        try {
                            System.out.print("Enter Rider ID: ");
                            String id = scanner.nextLine().trim();
                            if (id.isEmpty()) throw new IllegalArgumentException("Rider ID cannot be empty.");

                            if (createRider.getRiderMap().containsKey(id)) {
                                throw new IllegalArgumentException("Rider with ID '" + id + "' already exists.");
                            }

                            System.out.print("Enter Rider Name: ");
                            String name = scanner.nextLine().trim();
                            if (name.isEmpty()) throw new IllegalArgumentException("Rider name cannot be empty.");

                            System.out.print("Enter Reliability Rating (0 to 5): ");
                            String ratingInput = scanner.nextLine().trim();
                            int rating;
                            try {
                                rating = Integer.parseInt(ratingInput);
                                if (rating < 0 || rating > 5) {
                                    throw new IllegalArgumentException("Reliability rating must be between 0 and 5.");
                                }
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Reliability rating must be a valid number.");
                            }

                            System.out.print("Can Handle Fragile Items? (Y/N): ");
                            String fragileInput = scanner.nextLine().trim().toLowerCase();
                            boolean canHandleFragile;
                            if (fragileInput.equals("y")) {
                                canHandleFragile = true;
                            } else if (fragileInput.equals("n")) {
                                canHandleFragile = false;
                            } else {
                                throw new IllegalArgumentException("Invalid input for fragile support. Use Y or N.");
                            }

                            createRider.createRider(id, name, rating, canHandleFragile);

                        } catch (IllegalArgumentException e) {
                            System.err.println("Error: " + e.getMessage());
                            System.err.flush();
                        } catch (Exception e) {
                            System.err.println("Unexpected error: " + e.getMessage());
                            System.err.flush();
                        }
                        break;

                    case "2":
                        try {
                            System.out.print("Enter Package ID: ");
                            String id = scanner.nextLine();

                            System.out.print("Enter Receiver's Name: ");
                            String receiverName = scanner.nextLine();

                            System.out.print("Enter Receiver's Address: ");
                            String receiverAddress = scanner.nextLine();

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

                            System.out.print("Enter Payment Status (PREPAID/COD): ");
                            PackagePaymentStatus packagePaymentStatus;

                            try {
                                String input = scanner.nextLine().toUpperCase();
                                packagePaymentStatus = PackagePaymentStatus.valueOf(input);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Invalid input for 'Payment Status'. Please enter PREPAID or COD.");
                                System.err.flush();
                                break;
                            }

                            createPackage.placeOrder(id, receiverName, receiverAddress, priority, fragile, volume, packagePaymentStatus);

                        } catch (CreatePackage.PackageAlreadyExistsException |
                                 CreatePackage.InvalidPackageDataException e) {
                            System.err.println("Error placing package: " + e.getMessage());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid input: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Unexpected error: " + e.getMessage());
                        }
                        break;

                    case "3":
                        if (packagePriority.getPendingQueue().isEmpty()) {
                            System.out.println("No pending packages in the queue.");
                        } else {
                            PriorityQueue<Package> tempQueue = new PriorityQueue<>(packagePriority.getPendingQueue());

                            System.out.println("\n Pending Package Priority Queue:");
                            System.out.println("-----------------------------------------------------------");

                            while (!tempQueue.isEmpty()) {
                                Package pkg = tempQueue.poll();
                                System.out.println("Package ID     : " + pkg.getId());
                                System.out.println("Priority       : " + pkg.getPriority());
                                System.out.println("Deadline       : " + new Date(pkg.getDeadline()));
                                System.out.println("Volume         : " + pkg.getVolume());
                                System.out.println("Fragile        : " + (pkg.isFragile() ? "Yes" : "No"));
                                System.out.println("Payment Status : " + pkg.getPackagePaymentStatus());
                                System.out.println("Status         : " + pkg.getStatus());
                                System.out.println("-----------------------------------------------------------");
                            }
                        }
                        break;

                    case "4":
                        AssignPackageToRider assignService = new AssignPackageToRider();
                        assignService.assignPackageToRider(packagePriority, createRider);
                        break;

                    case "5":
                        RidersAndAssignedPackages.viewPackagesForRider(createRider, createPackage.getAllPackages());
                        break;
                        
                    case "6":
                        break;

                    case "7":
                        System.out.println("Exiting Chronos Couriers...");
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 or 7.");
                }
            }

            scanner.close();

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
