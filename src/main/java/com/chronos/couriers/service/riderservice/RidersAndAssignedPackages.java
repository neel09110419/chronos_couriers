package com.chronos.couriers.service.riderservice;

import com.chronos.couriers.model.riderinfo.Rider;
import com.chronos.couriers.model.packageinfo.Package;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class RidersAndAssignedPackages {

    public static void viewPackagesForRider(CreateRider createRider, Collection<Package> allPackages) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Rider ID to view their assigned packages: ");
        String riderId = scanner.nextLine().trim();

        Rider rider = createRider.getRiderMap().get(riderId);

        if (rider == null) {
            System.out.println("Rider with ID '" + riderId + "' not found.");
            return;
        }

        System.out.println("\n--- Rider Details ---");
        System.out.println("Rider ID            : " + rider.getRiderId());
        System.out.println("Name                : " + rider.getRiderName());
        System.out.println("Status              : " + rider.getRiderStatus());
        System.out.println("Reliability         : " + rider.getReliabilityRating());
        System.out.println("Fragile Capable     : " + (rider.isCanHandleFragile() ? "Yes" : "No"));
        System.out.println("Load                : " + rider.getCurrentVolumeLoad() + "/" + rider.getMaxLoad());

        List<Package> assignedPackages = allPackages.stream()
                .filter(p -> riderId.equals(p.getAssignedRiderId()))
                .toList();

        System.out.println("\n--- Assigned Packages ---");

        if (assignedPackages.isEmpty()) {
            System.out.println("No packages assigned to this rider.");
        } else {
            for (Package pkg : assignedPackages) {
                System.out.println("--------------------------------------------------");
                System.out.println("Package ID              : " + pkg.getId());
                System.out.println("Receiver's Name         : " + pkg.getReceiverName());
                System.out.println("Receiver's Address      : " + pkg.getReceiverAddress());
                System.out.println("Priority                : " + pkg.getPriority());
                System.out.println("Volume                  : " + pkg.getVolume());
                System.out.println("Fragile                 : " + (pkg.isFragile() ? "Yes" : "No"));
                System.out.println("Payment Status          : " + pkg.getPackagePaymentStatus());
                System.out.println("Status                  : " + pkg.getStatus());
            }
            System.out.println("--------------------------------------------------");
        }
    }
}