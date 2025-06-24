package com.chronos.couriers.service;

import com.chronos.couriers.model.PackagePriorityType;
import com.chronos.couriers.model.Package;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreatePackage {

    private final Map<String, Package> packageMap = new HashMap<>();

    public void placeOrder(String id, PackagePriorityType priority, boolean fragile)
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

            Package newPackage = new Package(id, priority, orderTime, deadline, fragile);
            packageMap.put(id, newPackage);

            System.out.println("Package '" + id + "' placed with priority " + priority +
                    " and deadline " + new Date(deadline));

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