package com.chronos.couriers.service.audit;

import com.chronos.couriers.model.packageinfo.PackagePriorityType;
import com.chronos.couriers.model.packageinfo.PackageStatus;
import com.chronos.couriers.model.packageinfo.Package;
import com.chronos.couriers.service.packageservice.PackagePriority;

import java.util.Date;

public class AuditService {
    public void getMissedExpressDeliveries(PackagePriority packagePriority) {
        long now = System.currentTimeMillis();
        boolean found = false;

        for (Package pkg : packagePriority.getPendingQueue()) {
            if (pkg.getPriority() == PackagePriorityType.EXPRESS &&
                    pkg.getStatus() == PackageStatus.DELIVERED &&
                    pkg.getDeliveryTime() > pkg.getDeadline()) {

                found = true;
                System.out.println("Package ID     : " + pkg.getId());
                System.out.println("Delivered Late : " + new Date(pkg.getDeliveryTime()));
                System.out.println("Deadline       : " + new Date(pkg.getDeadline()));
                System.out.println("--------------------------------------");
            }
        }

        if (!found) {
            System.out.println("No EXPRESS packages missed their delivery window.");
        }
    }
}

