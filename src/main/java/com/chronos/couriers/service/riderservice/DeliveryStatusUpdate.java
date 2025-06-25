package com.chronos.couriers.service.riderservice;

import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;
import com.chronos.couriers.model.packageinfo.PackageStatus;
import com.chronos.couriers.model.riderinfo.Rider;
import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.model.riderinfo.RiderStatus;

import java.util.List;
import java.util.Scanner;

public class DeliveryStatusUpdate {

    public static void updatePackageStatus(CreateRider createRider, List<Package> allPackages) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Rider ID to update package status: ");
        String riderId = scanner.nextLine().trim();

        Rider rider = createRider.getRiderMap().get(riderId);
        if (rider == null) {
            System.out.println("Rider not found.");
            return;
        }

        List<Package> assignedPackages = allPackages.stream()
                .filter(p -> riderId.equals(p.getAssignedRiderId()))
                .filter(p -> p.getStatus() == PackageStatus.OUT_FOR_DELIVERY)
                .toList();

        if (assignedPackages.isEmpty()) {
            System.out.println("No active packages found for this rider.");
            return;
        }

        System.out.println("\nPackages assigned to " + rider.getRiderName() + ":");
        for (Package pkg : assignedPackages) {
            System.out.println("- " + pkg.getId() + " | Volume: " + pkg.getVolume() +
                    " | Payment: " + pkg.getPackagePaymentStatus());
        }

        System.out.print("\nEnter Package ID to update status: ");
        String packageId = scanner.nextLine().trim();

        Package selectedPackage = assignedPackages.stream()
                .filter(p -> p.getId().equals(packageId))
                .findFirst()
                .orElse(null);

        if (selectedPackage == null) {
            System.out.println("Invalid Package ID.");
            return;
        }

        if (selectedPackage.getPackagePaymentStatus() == PackagePaymentStatus.COD) {
            System.out.print("Was payment received? (Y/N): ");
            String paidInput = scanner.nextLine().trim().toUpperCase();
            if (paidInput.equals("Y")) {
                selectedPackage.setDeliveryTime(System.currentTimeMillis());
                selectedPackage.setStatus(PackageStatus.DELIVERED);
            } else if (paidInput.equals("N")) {
                selectedPackage.setStatus(PackageStatus.CANCELLED);
            } else {
                System.out.println("Invalid input. Enter Y or N.");
                return;
            }
        } else {
            System.out.print("Update status to DELIVERED or CANCELLED? (D/C): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            switch (statusInput) {
                case "D":
                    selectedPackage.setDeliveryTime(System.currentTimeMillis());
                    selectedPackage.setStatus(PackageStatus.DELIVERED);
                    break;
                case "C":
                    selectedPackage.setStatus(PackageStatus.CANCELLED);
                    break;
                default:
                    System.out.println("Invalid choice. Only D or C allowed.");
                    return;
            }
        }

        rider.setCurrentVolumeLoad(rider.getCurrentVolumeLoad() - selectedPackage.getVolume());
        System.out.println("Package " + packageId + " marked as " + selectedPackage.getStatus());

        boolean anyRemaining = allPackages.stream()
                .anyMatch(p -> riderId.equals(p.getAssignedRiderId()) && p.getStatus() == PackageStatus.OUT_FOR_DELIVERY);

        if (!anyRemaining) {
            rider.setRiderStatus(RiderStatus.AVAILABLE);
            System.out.println("Rider " + rider.getRiderName() + " is now AVAILABLE.");
        }
    }
}