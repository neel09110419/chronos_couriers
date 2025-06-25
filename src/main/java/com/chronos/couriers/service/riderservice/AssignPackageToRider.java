package com.chronos.couriers.service.riderservice;

import com.chronos.couriers.model.riderinfo.Rider;
import com.chronos.couriers.model.riderinfo.RiderStatus;
import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.model.packageinfo.PackageStatus;
import com.chronos.couriers.service.packageservice.PackagePriority;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AssignPackageToRider {

    public void assignPackageToRider(PackagePriority packagePriority, CreateRider createRider) {

        PriorityQueue<Package> tempQueue = new PriorityQueue<>(packagePriority.getPendingQueue());

        if (tempQueue.isEmpty()) {
            System.out.println("No pending packages to assign.");
            return;
        }

        boolean assignedAny = false;

        while (!tempQueue.isEmpty()) {
            Package pkg = tempQueue.poll();

            // Skip if package is not PENDING
            if (pkg.getStatus() != PackageStatus.PENDING) continue;

            boolean assigned = false;

            for (Rider rider : createRider.getRiderMap().values().stream()
                    .filter(r -> r.getRiderStatus() == RiderStatus.AVAILABLE)
                    .sorted(Comparator.comparingInt(Rider::getReliabilityRating).reversed())
                    .toList()) {

                int remainingCapacity = rider.getMaxLoad() - rider.getCurrentVolumeLoad();

                // Skip if volume exceeds rider's remaining capacity
                if (pkg.getVolume() > remainingCapacity) continue;

                boolean fragileCompatible = true;

                if (pkg.isFragile()) {
                    fragileCompatible = rider.isCanHandleFragile();
                } else {
                    // Check if fragile packages are still pending
                    boolean fragilePackagesRemain = tempQueue.stream().anyMatch(Package::isFragile);

                    // Count available fragile-capable riders excluding current rider
                    long otherFragileCapableRiders = createRider.getRiderMap().values().stream()
                            .filter(r -> r != rider)
                            .filter(r -> r.getRiderStatus() == RiderStatus.AVAILABLE)
                            .filter(Rider::isCanHandleFragile)
                            .count();


                    if (!fragilePackagesRemain && rider.isCanHandleFragile()) {
                        if (pkg.getVolume() > (100 - rider.getCurrentVolumeLoad())) continue;

                        fragileCompatible = true;
                    }
                }

                if (fragileCompatible) {
                    // Assign package
                    pkg.setAssignedRiderId(rider.getRiderId());
                    pkg.setStatus(PackageStatus.OUT_FOR_DELIVERY);
                    rider.setCurrentVolumeLoad(rider.getCurrentVolumeLoad() + pkg.getVolume());

                    System.out.println("Package '" + pkg.getId() + "' assigned to Rider '" + rider.getRiderName()
                            + "' (ID: " + rider.getRiderId() + "), Volume: " + pkg.getVolume()
                            + " -> Status: OUT_FOR_DELIVERY");

                    assigned = true;
                    assignedAny = true;

                    // Check if rider can carry any more packages
                    int updatedRemaining = rider.getMaxLoad() - rider.getCurrentVolumeLoad();
                    boolean canHandleMore = tempQueue.stream().anyMatch(p -> {
                        boolean compatible = !p.isFragile() || rider.isCanHandleFragile();
                        return p.getVolume() <= updatedRemaining && compatible;
                    });

                    if (!canHandleMore) {
                        rider.setRiderStatus(RiderStatus.BUSY);
                        System.out.println("Rider '" + rider.getRiderName()
                                + "' marked OUT_FOR_DELIVERY (Load: " + rider.getCurrentVolumeLoad() + ")");
                    }

                    break;
                }
            }

            if (!assigned) {
                System.out.println("No suitable rider found for Package '" + pkg.getId() + "'");
            }
        }

        if (!assignedAny) {
            System.out.println("No packages were assigned. Riders might be full or not compatible.");
        }
    }
}
