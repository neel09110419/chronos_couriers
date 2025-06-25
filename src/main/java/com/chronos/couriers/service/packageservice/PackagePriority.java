package com.chronos.couriers.service.packageservice;

import java.util.Comparator;
import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.model.packageinfo.PackageStatus;

import java.util.PriorityQueue;

public class PackagePriority {

    private final PriorityQueue<Package> pendingQueue = new PriorityQueue<>(
            Comparator
                    .comparing(Package::getPriority)                    // EXPRESS > STANDARD
                    .thenComparing(Package::getDeadline)                // Sooner deadline first
                    .thenComparing(Package::getOrderTime)               // Older orders first
    );

    public void addPackage(Package pkg) {
        if (pkg.getStatus() == PackageStatus.DELIVERED || pkg.getStatus() == PackageStatus.CANCELLED) {
            return; // Do not add completed/cancelled packages
        }

        pendingQueue.add(pkg);
        System.out.println("Package '" + pkg.getId() + "' added to priority queue.");
    }

    public PriorityQueue<Package> getPendingQueue() {
        return pendingQueue;
    }
}