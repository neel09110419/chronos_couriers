package com.chronos.couriers.service.packageservice;

import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;
import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import com.chronos.couriers.model.packageinfo.Package;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreatePackage {

    private final Map<String, Package> packageMap = new HashMap<>();

    private final PackagePriority packagePriority;

    public CreatePackage(PackagePriority packagePriority) {
        this.packagePriority = packagePriority;
    }

    public void placeOrder(String id, String receiverName, String receiverAddress, PackagePriorityType priority, boolean fragile, int volume, PackagePaymentStatus packagePaymentStatus)
            throws PackageAlreadyExistsException, InvalidPackageDataException {
        try {
            if (id == null || priority == null) {
                throw new InvalidPackageDataException("Package ID and priority must not be null.");
            }

            if (packageMap.containsKey(id)) {
                throw new PackageAlreadyExistsException("Package with ID '" + id + "' already exists.");
            }

            long orderTime = System.currentTimeMillis();
            long deadline = (priority == PackagePriorityType.EXPRESS)
                    ? orderTime + 1 * 24 * 60 * 60 * 1000L
                    : orderTime + 3 * 24 * 60 * 60 * 1000L;

            Package newPackage = new Package(id, receiverName, receiverAddress, priority, orderTime, deadline, fragile, volume, packagePaymentStatus);
            packageMap.put(id, newPackage);

            System.out.println("\nPackage Created Successfully!");
            System.out.println("------------------------------------");
            System.out.println("Package ID     : " + id);
            System.out.println("Priority       : " + priority);
            System.out.println("Fragile        : " + (fragile ? "Yes" : "No"));
            System.out.println("Volume         : " + volume);
            System.out.println("Payment Status : " + packagePaymentStatus);
            System.out.println("Order Time     : " + new Date(orderTime));
            System.out.println("Deadline       : " + new Date(deadline));
            System.out.println("------------------------------------\n");

            packagePriority.addPackage(newPackage);

        } catch (PackageAlreadyExistsException | InvalidPackageDataException e) {
            throw e; // known custom exceptions
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during package placement.", e);
        }
    }

    public Package getPackage(String id) {
        return packageMap.get(id);
    }

    public Collection<Package> getAllPackages() {
        return packageMap.values();
    }



    public static class PackageAlreadyExistsException extends Exception {
        public PackageAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InvalidPackageDataException extends Exception {
        public InvalidPackageDataException(String message) {
            super(message);
        }
    }
}