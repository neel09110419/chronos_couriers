package com.chronos.couriers.service.riderservice;

import com.chronos.couriers.model.riderinfo.Rider;

import java.util.HashMap;
import java.util.Map;

public class CreateRider {

    private final Map<String, Rider> riderMap = new HashMap<>();

    public void createRider(String riderId, String riderName, int reliabilityRating, boolean canHandleFragile) {

        Rider newRider = new Rider(riderId, riderName, reliabilityRating, canHandleFragile);
        riderMap.put(riderId, newRider);

        System.out.println("\nRider Created Successfully!");
        System.out.println("------------------------------------");
        System.out.println("Rider ID          : " + riderId);
        System.out.println("Name              : " + riderName);
        System.out.println("Reliability Rating: " + reliabilityRating);
        System.out.println("Can Handle Fragile: " + (canHandleFragile ? "Yes" : "No"));
        System.out.println("------------------------------------\n");
    }

    public Map<String, Rider> getRiderMap() {
        return riderMap;
    }
}
