package com.chronos.couriers.service.riderservice;

import com.chronos.couriers.model.riderinfo.Rider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateRiderTest {

    private CreateRider createRider;

    @BeforeEach
    void setup() {
        createRider = new CreateRider();
    }

    @Test
    void testCreateRiderSuccessfully() {
        createRider.createRider("R1", "Rider 1", 4, true);

        Map<String, Rider> riderMap = createRider.getRiderMap();
        assertEquals(1, riderMap.size());

        Rider rider = riderMap.get("R1");
        assertNotNull(rider);
        assertEquals("R1", rider.getRiderId());
        assertEquals("Rider 1", rider.getRiderName());
        assertEquals(4, rider.getReliabilityRating());
        assertTrue(rider.isCanHandleFragile());
    }

    @Test
    void testCreateDuplicateRiderIdReplacesOldOne() {
        createRider.createRider("R1", "Rider 1", 4, true);
        createRider.createRider("R1", "Rider 2", 5, false);

        Map<String, Rider> riderMap = createRider.getRiderMap();
        assertEquals(1, riderMap.size());

        Rider rider = riderMap.get("R1");
        assertEquals("Rider 2", rider.getRiderName());
        assertEquals(5, rider.getReliabilityRating());
        assertFalse(rider.isCanHandleFragile());
    }
}
