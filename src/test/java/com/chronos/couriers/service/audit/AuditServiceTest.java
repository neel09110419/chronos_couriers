package com.chronos.couriers.service.audit;

import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import com.chronos.couriers.model.packageinfo.PackageStatus;
import com.chronos.couriers.service.packageservice.PackagePriority;
import com.chronos.couriers.model.packageinfo.PackagePaymentStatus;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AuditServiceTest {

    private AuditService auditService;

    @BeforeEach
    void setup() {
        auditService = new AuditService();
    }

    @Test
    void testMissedExpressDeliveryDetected() {
        long now = System.currentTimeMillis();
        long oneHourAgo = now - 3600000;

        Package lateExpressPkg = new Package("PKG001", "Rec1", "123 Test Street", PackagePriorityType.EXPRESS, oneHourAgo, oneHourAgo, false, 10, PackagePaymentStatus.PREPAID);
        lateExpressPkg.setStatus(PackageStatus.DELIVERED);
        lateExpressPkg.setDeliveryTime(now);

        PackagePriority priorityQueue = new PackagePriority();
        priorityQueue.getPendingQueue().add(lateExpressPkg);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        auditService.getMissedExpressDeliveries(priorityQueue);

        String output = outContent.toString();
        assertTrue(output.contains("Package ID     : PKG001"));
        assertTrue(output.contains("Delivered Late"));
        assertTrue(output.contains("Deadline"));
    }

    @Test
    void testNoMissedDeliveries() {
        long now = System.currentTimeMillis();
        long futureDeadline = now + 3600000;

        Package onTimePkg = new Package("PKG002", "Rec 2", "456 Test Street", PackagePriorityType.EXPRESS, now, futureDeadline, false, 8, PackagePaymentStatus.COD);
        onTimePkg.setStatus(PackageStatus.DELIVERED);
        onTimePkg.setDeliveryTime(now);

        PackagePriority priorityQueue = new PackagePriority();
        priorityQueue.getPendingQueue().add(onTimePkg);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        auditService.getMissedExpressDeliveries(priorityQueue);

        String output = outContent.toString();
        assertTrue(output.contains("No EXPRESS packages missed their delivery window."));
    }
}
