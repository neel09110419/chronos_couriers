package com.chronos.couriers.service.riderservice;

import com.chronos.couriers.model.riderinfo.Rider;
import com.chronos.couriers.model.riderinfo.RiderStatus;
import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.service.packageservice.PackagePriority;
import com.chronos.couriers.model.packageinfo.PackageStatus;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AssignPackageToRider {

    public void assignPackageToRider(PackagePriority packagePriority, CreateRider createRider) {

        PriorityQueue<Package> tempQueue = new PriorityQueue<>(packagePriority.getPendingQueue());

        if (tempQueue.isEmpty()) {
            System.out.println("No pending packages to assignPackageToRider.");
            return;
        }

        boolean assignedAny = false;

        while (!tempQueue.isEmpty()) {
            Package pkg = tempQueue.poll();
            if (!(pkg.getStatus() == PackageStatus.PENDING)) {
                continue;
            }
            boolean assigned = false;

            for (Rider rider : createRider.getRiderMap().values().stream()
                    .filter(r -> r.getRiderStatus() == RiderStatus.AVAILABLE)
                    .sorted(Comparator.comparingInt(Rider::getReliabilityRating).reversed())
                    .toList()) {

                int remainingCapacity = rider.getMaxLoad() - rider.getCurrentVolumeLoad();

                boolean fragileCompatible = true;

                if (pkg.isFragile()) {
                    fragileCompatible = rider.isCanHandleFragile();
                } else {

                    boolean fragilePackagesRemain = tempQueue.stream().anyMatch(Package::isFragile);
                    long availableFragileRiders = createRider.getRiderMap().values().stream()
                            .filter(r -> r.getRiderStatus() == RiderStatus.AVAILABLE)
                            .filter(Rider::isCanHandleFragile)
                            .count();

                    if (fragilePackagesRemain && rider.isCanHandleFragile() && availableFragileRiders == 1) {
                        fragileCompatible = false;
                    }
                }

                if (remainingCapacity >= pkg.getVolume() && fragileCompatible) {
                    //  Assign package
                    pkg.setAssignedRiderId(rider.getRiderId());
                    pkg.setStatus(PackageStatus.OUT_FOR_DELIVERY);
                    rider.setCurrentVolumeLoad(rider.getCurrentVolumeLoad() + pkg.getVolume());

                    System.out.println("Package '" + pkg.getId() + "' assigned to Rider '" + rider.getRiderName()
                            + "' (ID: " + rider.getRiderId() + "), Volume: " + pkg.getVolume()
                            + " -> Status: OUT_FOR_DELIVERY");

                    assigned = true;
                    assignedAny = true;

                    // Check if rider can take more packages
                    int updatedRemaining = rider.getMaxLoad() - rider.getCurrentVolumeLoad();
                    boolean canHandleMore = tempQueue.stream().anyMatch(p -> {
                        boolean compatible = !p.isFragile() || rider.isCanHandleFragile(); // relaxation for non-fragile packages
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
