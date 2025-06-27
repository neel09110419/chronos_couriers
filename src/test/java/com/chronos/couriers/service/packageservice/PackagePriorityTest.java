package com.chronos.couriers.service.packageservice;

import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;
import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import com.chronos.couriers.model.packageinfo.PackageStatus;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class PackagePriorityTest {

    @Test
    void testAddValidPackage() {
        PackagePriority packagePriority = new PackagePriority();

        Package pkg = new Package("PKG001", "Rec 1", "123 Test Street", PackagePriorityType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 10000, false, 10, PackagePaymentStatus.PREPAID);

        packagePriority.addPackage(pkg);
        assertEquals(1, packagePriority.getPendingQueue().size());
    }

    @Test
    void testDeliveredPackageIsNotAdded() {
        PackagePriority packagePriority = new PackagePriority();

        Package pkg = new Package("PKG002", "Rec 2", "456 Test Street", PackagePriorityType.STANDARD, System.currentTimeMillis(), System.currentTimeMillis() + 10000, false, 8, PackagePaymentStatus.COD);
        pkg.setStatus(PackageStatus.DELIVERED);

        packagePriority.addPackage(pkg);
        assertEquals(0, packagePriority.getPendingQueue().size());
    }

    @Test
    void testCancelledPackageIsNotAdded() {
        PackagePriority packagePriority = new PackagePriority();

        Package pkg = new Package("PKG003", "Rec 3", "789 Test Street", PackagePriorityType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 10000, true, 12, PackagePaymentStatus.PREPAID);
        pkg.setStatus(PackageStatus.CANCELLED);

        packagePriority.addPackage(pkg);
        assertEquals(0, packagePriority.getPendingQueue().size());
    }

    @Test
    void testQueueOrderingByPriorityDeadlineAndOrderTime() {
        PackagePriority packagePriority = new PackagePriority();

        long now = System.currentTimeMillis();

        Package standardLater = new Package("PKG1", "A", "Addr", PackagePriorityType.STANDARD, now, now + 30000, false, 5, PackagePaymentStatus.PREPAID);
        Package expressSooner = new Package("PKG2", "B", "Addr", PackagePriorityType.EXPRESS, now, now + 10000, false, 5, PackagePaymentStatus.PREPAID);
        Package expressLater = new Package("PKG3", "C", "Addr", PackagePriorityType.EXPRESS, now, now + 20000, false, 5, PackagePaymentStatus.PREPAID);

        packagePriority.addPackage(standardLater);
        packagePriority.addPackage(expressSooner);
        packagePriority.addPackage(expressLater);

        PriorityQueue<Package> queue = packagePriority.getPendingQueue();
        assertEquals("PKG2", queue.poll().getId());
        assertEquals("PKG3", queue.poll().getId());
        assertEquals("PKG1", queue.poll().getId());
    }
}
