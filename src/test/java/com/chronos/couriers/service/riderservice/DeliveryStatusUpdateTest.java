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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusUpdateTest {

    private CreateRider createRider;
    private Rider rider;
    private List<Package> packages;

    @BeforeEach
    void setup() {
        createRider = new CreateRider();
        rider = new Rider("R1", "Rider 1", 5, true);
        rider.setRiderStatus(RiderStatus.BUSY);
        createRider.getRiderMap().put("R1", rider);

        packages = new ArrayList<>();

        Package pkg = new Package("PKG1", "Rec 1", "Address 1", PackagePriorityType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 10000, false, 10, PackagePaymentStatus.COD);
        pkg.setAssignedRiderId("R1");
        pkg.setStatus(PackageStatus.OUT_FOR_DELIVERY);
        rider.setCurrentVolumeLoad(10);

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
    void testCodDelivered() {
        withMockedInput("R1\nPKG1\nY\n", () -> {
            DeliveryStatusUpdate.updatePackageStatus(createRider, packages);
        });

        Package pkg = packages.get(0);
        assertEquals(PackageStatus.DELIVERED, pkg.getStatus());
        assertTrue(pkg.getDeliveryTime() > 0);
        assertEquals(0, rider.getCurrentVolumeLoad());
        assertEquals(RiderStatus.AVAILABLE, rider.getRiderStatus());
    }

    @Test
    void testCodCancelled() {
        withMockedInput("R1\nPKG1\nN\n", () -> {
            DeliveryStatusUpdate.updatePackageStatus(createRider, packages);
        });

        Package pkg = packages.get(0);
        assertEquals(PackageStatus.CANCELLED, pkg.getStatus());
        assertEquals(0, rider.getCurrentVolumeLoad());
        assertEquals(RiderStatus.AVAILABLE, rider.getRiderStatus());
    }

    @Test
    void testPrepaidDelivered() {
        Package pkg = packages.get(0);
        pkg.setPackagePaymentStatus(PackagePaymentStatus.PREPAID);

        withMockedInput("R1\nPKG1\nD\n", () -> {
            DeliveryStatusUpdate.updatePackageStatus(createRider, packages);
        });

        assertEquals(PackageStatus.DELIVERED, pkg.getStatus());
        assertEquals(RiderStatus.AVAILABLE, rider.getRiderStatus());
    }

    @Test
    void testPrepaidCancelled() {
        Package pkg = packages.get(0);
        pkg.setPackagePaymentStatus(PackagePaymentStatus.PREPAID);

        withMockedInput("R1\nPKG1\nC\n", () -> {
            DeliveryStatusUpdate.updatePackageStatus(createRider, packages);
        });

        assertEquals(PackageStatus.CANCELLED, pkg.getStatus());
        assertEquals(RiderStatus.AVAILABLE, rider.getRiderStatus());
    }

    @Test
    void testInvalidPackageId() {
        withMockedInput("R1\nINVALID_ID\n", () -> {
            DeliveryStatusUpdate.updatePackageStatus(createRider, packages);
        });

        assertEquals(PackageStatus.OUT_FOR_DELIVERY, packages.get(0).getStatus());
        assertEquals(RiderStatus.BUSY, rider.getRiderStatus());
    }

    @Test
    void testInvalidCodInput() {
        withMockedInput("R1\nPKG1\nX\n", () -> {
            DeliveryStatusUpdate.updatePackageStatus(createRider, packages);
        });

        assertEquals(PackageStatus.OUT_FOR_DELIVERY, packages.get(0).getStatus());
    }

    @Test
    void testInvalidPrepaidInput() {
        Package pkg = packages.get(0);
        pkg.setPackagePaymentStatus(PackagePaymentStatus.PREPAID);

        withMockedInput("R1\nPKG1\nX\n", () -> {
            DeliveryStatusUpdate.updatePackageStatus(createRider, packages);
        });

        assertEquals(PackageStatus.OUT_FOR_DELIVERY, pkg.getStatus());
    }

    @Test
    void testInvalidRiderId() {
        withMockedInput("INVALID\n", () -> {
            DeliveryStatusUpdate.updatePackageStatus(createRider, packages);
        });

        assertEquals(PackageStatus.OUT_FOR_DELIVERY, packages.get(0).getStatus());
        assertEquals(RiderStatus.BUSY, rider.getRiderStatus());
    }
}