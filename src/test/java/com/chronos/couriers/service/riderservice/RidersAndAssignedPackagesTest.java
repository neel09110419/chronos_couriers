package com.chronos.couriers.service.riderservice;

import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;
import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import com.chronos.couriers.model.packageinfo.PackageStatus;
import com.chronos.couriers.model.riderinfo.Rider;
import com.chronos.couriers.model.riderinfo.RiderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

class RidersAndAssignedPackagesTest {

    private CreateRider createRider;
    private List<Package> packages;

    @BeforeEach
    void setup() {
        createRider = new CreateRider();
        packages = new ArrayList<>();

        Rider rider = new Rider("R1", "Rider 1", 5, true);
        rider.setCurrentVolumeLoad(20);
        rider.setRiderStatus(RiderStatus.BUSY);
        createRider.getRiderMap().put("R1", rider);

        Package pkg = new Package("PKG1", "Rec 1", "Street A", PackagePriorityType.EXPRESS,
                System.currentTimeMillis(), System.currentTimeMillis() + 5000,
                true, 20, PackagePaymentStatus.COD);
        pkg.setAssignedRiderId("R1");
        pkg.setStatus(PackageStatus.OUT_FOR_DELIVERY);

        packages.add(pkg);
    }

    private void withMockedInput(String simulatedInput, Runnable block) {
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
            block.run();
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testRiderWithPackages() {
        withMockedInput("R1\n", () -> {
            RidersAndAssignedPackages.viewPackagesForRider(createRider, packages);
        });
    }

    @Test
    void testRiderWithNoPackages() {
        packages.clear();

        withMockedInput("R1\n", () -> {
            RidersAndAssignedPackages.viewPackagesForRider(createRider, packages);
        });
    }

    @Test
    void testInvalidRiderId() {
        withMockedInput("INVALID_ID\n", () -> {
            RidersAndAssignedPackages.viewPackagesForRider(createRider, packages);
        });
    }
}