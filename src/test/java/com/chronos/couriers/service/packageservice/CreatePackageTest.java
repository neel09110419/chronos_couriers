package com.chronos.couriers.service.packageservice;

import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;
import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreatePackageTest {

    private PackagePriority mockPriority;
    private CreatePackage createPackage;

    @BeforeEach
    void setup() {
        mockPriority = mock(PackagePriority.class);
        createPackage = new CreatePackage(mockPriority);
    }

    @Test
    void testPlaceOrderSuccess() throws Exception {
        createPackage.placeOrder("PKG001", "Rec 1", "123 Test Street", PackagePriorityType.EXPRESS, true, 15, PackagePaymentStatus.PREPAID);

        Package pkg = createPackage.getPackage("PKG001");
        assertNotNull(pkg);
        assertEquals("PKG001", pkg.getId());
        assertEquals(PackagePriorityType.EXPRESS, pkg.getPriority());
        assertEquals(PackagePaymentStatus.PREPAID, pkg.getPackagePaymentStatus());
        verify(mockPriority, times(1)).addPackage(pkg);
    }

    @Test
    void testDuplicatePackageThrowsException() throws Exception {
        createPackage.placeOrder("PKG001", "Rec 1", "123 Test Street", PackagePriorityType.EXPRESS, true, 15, PackagePaymentStatus.PREPAID);

        Exception exception = assertThrows(CreatePackage.PackageAlreadyExistsException.class, () -> createPackage.placeOrder("PKG001", "Rec 1", "456 Test Street", PackagePriorityType.STANDARD, false, 10, PackagePaymentStatus.COD));

        assertEquals("Package with ID 'PKG001' already exists.", exception.getMessage());
    }

    @Test
    void testNullIdThrowsException() {
        Exception exception = assertThrows(CreatePackage.InvalidPackageDataException.class, () -> createPackage.placeOrder(null, "Rec 1", "123 Test Street", PackagePriorityType.EXPRESS, true, 15, PackagePaymentStatus.PREPAID));

        assertEquals("Package ID and priority must not be null.", exception.getMessage());
    }

    @Test
    void testNullPriorityThrowsException() {
        Exception exception = assertThrows(CreatePackage.InvalidPackageDataException.class, () -> createPackage.placeOrder("PKG002", "Rec 2", "123 Test Street", null, true, 15, PackagePaymentStatus.PREPAID));

        assertEquals("Package ID and priority must not be null.", exception.getMessage());
    }

    @Test
    void testGetAllPackagesReturnsCorrectCount() throws Exception {
        createPackage.placeOrder("PKG001", "Rec 1", "123 Test Street", PackagePriorityType.EXPRESS, true, 15, PackagePaymentStatus.PREPAID);
        createPackage.placeOrder("PKG002", "Rec 2", "456 Test Street", PackagePriorityType.STANDARD, false, 10, PackagePaymentStatus.COD);

        Collection<Package> allPackages = createPackage.getAllPackages();
        assertEquals(2, allPackages.size());
    }
}