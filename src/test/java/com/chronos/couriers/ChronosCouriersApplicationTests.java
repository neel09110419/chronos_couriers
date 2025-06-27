package com.chronos.couriers;

import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;
import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import com.chronos.couriers.model.packageinfo.PackageStatus;
import com.chronos.couriers.service.audit.AuditService;
import com.chronos.couriers.service.packageservice.CreatePackage;
import com.chronos.couriers.service.packageservice.PackagePriority;
import com.chronos.couriers.service.riderservice.AssignPackageToRider;
import com.chronos.couriers.service.riderservice.CreateRider;
import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.service.riderservice.DeliveryStatusUpdate;
import com.chronos.couriers.service.riderservice.RidersAndAssignedPackages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChronosCouriersApplicationTests {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream((data + "\n").getBytes(StandardCharsets.UTF_8));
        System.setIn(testIn);
    }

    @Test
    void testCreateRiderFlow() {
        String input = String.join("\n", "1", "R1", "Rider 1", "4", "Y", "8");
        provideInput(input);
        ChronosCouriersApplication.main(new String[]{});
        assertTrue(outContent.toString().contains("Rider Created Successfully"));
    }

    @Test
    void testCreateOrderAndAssignPackage() {
        String input = String.join("\n", "1", "R2", "Rider 2", "5", "Y", "2", "PKG1", "Rec 1", "123 Street", "EXPRESS", "20", "N", "COD", "4", "8");
        provideInput(input);
        ChronosCouriersApplication.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Package Created Successfully"));
        assertTrue(output.contains("assigned to Rider"));
    }

    @Test
    void testInvalidMenuChoiceHandled() {
        provideInput("99\n8");
        ChronosCouriersApplication.main(new String[]{});
        assertTrue(outContent.toString().contains("Invalid choice"));
    }

    @Test
    void testPackageValidation_InvalidVolumeAndDuplicateID() {
        String input = String.join("\n", "2", "PKG2", "A", "Addr", "STANDARD", "150", "Y", "COD",
                "2", "PKG2", "A", "Addr", "STANDARD", "20", "Y", "COD",
                "2", "PKG2", "A", "Addr", "STANDARD", "20", "Y", "COD",
                "8");
        provideInput(input);
        ChronosCouriersApplication.main(new String[]{});
        String err = errContent.toString();
        assertTrue(err.contains("Volume must be between 1 and 100 inclusive."));
        assertTrue(err.contains("already exists"));
    }

    @Test
    void testViewPackagesByRiderId() {
        String input = String.join("\n", "1", "R4", "Rider 4", "3", "N", "2", "PKG5", "C", "Addr", "STANDARD", "10", "N", "PREPAID", "4", "5", "R4", "8");
        provideInput(input);
        ChronosCouriersApplication.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("--- Assigned Packages ---"));
        assertTrue(output.contains("PKG5"));
    }

    @Test
    void testNoCompatibleRider() throws Exception {
        PackagePriority priority = new PackagePriority();
        CreateRider createRider = new CreateRider();
        createRider.createRider("R7", "Rider 7", 1, false);

        CreatePackage createPackage = new CreatePackage(priority);
        createPackage.placeOrder("PKG9", "Z", "Address", PackagePriorityType.STANDARD, true, 10, PackagePaymentStatus.PREPAID);

        AssignPackageToRider assigner = new AssignPackageToRider();
        assigner.assignPackageToRider(priority, createRider);

        Package pkg = createPackage.getPackage("PKG9");
        assertNull(pkg.getAssignedRiderId());
    }

    @Test
    void testGetMissedExpressDeliveries_ShowsLateDelivery() {
        AuditService audit = new AuditService();

        long now = System.currentTimeMillis();
        long futureDeadline = now + 3600000;

        Package onTimePkg = new Package("PKG002", "Rec 2", "456 Test Street", PackagePriorityType.EXPRESS, now, futureDeadline, false, 8, PackagePaymentStatus.COD);
        onTimePkg.setStatus(PackageStatus.DELIVERED);
        onTimePkg.setDeliveryTime(now);

        PackagePriority priorityQueue = new PackagePriority();
        priorityQueue.getPendingQueue().add(onTimePkg);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        audit.getMissedExpressDeliveries(priorityQueue);

        String output = outContent.toString();
        assertTrue(output.contains("No EXPRESS packages missed their delivery window."));
    }

    @Test
    void testCODPaymentNotReceived_MarkedCancelled() {
        CreateRider createRider = new CreateRider();
        createRider.createRider("R6", "Rider 6", 5, true);

        Package pkg = new Package("PKGCOD", "Rec 1", "Addr", PackagePriorityType.STANDARD, 0, 0, false, 10, PackagePaymentStatus.COD);
        pkg.setAssignedRiderId("R6");
        pkg.setStatus(PackageStatus.OUT_FOR_DELIVERY);

        String input = String.join("\n", "R6", "PKGCOD", "N");
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        DeliveryStatusUpdate.updatePackageStatus(createRider, List.of(pkg));

        assertEquals(PackageStatus.CANCELLED, pkg.getStatus());
    }

    @Test
    void testInvalidRiderId() {
        CreateRider createRider = new CreateRider();
        String input = "XYZ\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        DeliveryStatusUpdate.updatePackageStatus(createRider, List.of());
    }

    @Test
    void testViewPackagesForRiderWithNoPackages() {
        CreateRider createRider = new CreateRider();
        createRider.createRider("R10", "Rider 10", 3, true);

        String input = "R10\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        RidersAndAssignedPackages.viewPackagesForRider(createRider, List.of());
    }
}
