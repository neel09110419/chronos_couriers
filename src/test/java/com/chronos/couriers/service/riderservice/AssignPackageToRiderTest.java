package com.chronos.couriers.service.riderservice;

import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;
import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import com.chronos.couriers.model.packageinfo.PackageStatus;
import com.chronos.couriers.model.riderinfo.Rider;
import com.chronos.couriers.model.riderinfo.RiderStatus;
import com.chronos.couriers.service.packageservice.PackagePriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssignPackageToRiderTest {

    private AssignPackageToRider assigner;
    private PackagePriority packagePriority;
    private CreateRider createRider;

    @BeforeEach
    void setup() {
        assigner = new AssignPackageToRider();
        packagePriority = new PackagePriority();
        createRider = new CreateRider();
    }

    @Test
    void testAssignPackageToAvailableRider() {
        Rider rider = new Rider("R1", "Rider 1", 4, true);
        createRider.getRiderMap().put("R1", rider);

        Package pkg = new Package("PKG1", "Rec 1", "Street A", PackagePriorityType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 10000, false, 10, PackagePaymentStatus.PREPAID);

        packagePriority.addPackage(pkg);

        assigner.assignPackageToRider(packagePriority, createRider);

        assertEquals("R1", pkg.getAssignedRiderId());
        assertEquals(PackageStatus.OUT_FOR_DELIVERY, pkg.getStatus());
        assertEquals(10, rider.getCurrentVolumeLoad());
    }

    @Test
    void testSkipIncompatibleFragileRider() {
        Rider rider = new Rider("R1", "Rider 1", 2, false);
        createRider.getRiderMap().put("R1", rider);

        Package pkg = new Package("PKG2", "Rec 2", "Street B", PackagePriorityType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 10000, true, 20, PackagePaymentStatus.COD);

        packagePriority.addPackage(pkg);

        assigner.assignPackageToRider(packagePriority, createRider);

        assertNull(pkg.getAssignedRiderId());
        assertEquals(PackageStatus.PENDING, pkg.getStatus());
    }

    @Test
    void testRiderMarkedBusyWhenFull() {
        Rider rider = new Rider("R2", "Rider 2", 4, true);
        createRider.getRiderMap().put("R2", rider);

        Package pkg1 = new Package("PKG3", "Rec 3", "C Street", PackagePriorityType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 10000, false, 10, PackagePaymentStatus.PREPAID);
        Package pkg2 = new Package("PKG4", "Rec 4", "D Street", PackagePriorityType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 20000, false, 1, PackagePaymentStatus.COD);

        packagePriority.addPackage(pkg1);
        packagePriority.addPackage(pkg2);

        assigner.assignPackageToRider(packagePriority, createRider);

        assertEquals("R2", pkg1.getAssignedRiderId());
        assertEquals(PackageStatus.OUT_FOR_DELIVERY, pkg1.getStatus());
        assertEquals(RiderStatus.BUSY, rider.getRiderStatus());
    }

    @Test
    void testNoPackagesToAssign() {
        assigner.assignPackageToRider(packagePriority, createRider);
    }
}
