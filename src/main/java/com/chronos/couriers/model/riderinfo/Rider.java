package com.chronos.couriers.model.riderinfo;

public class Rider {
    private final String riderId;
    private final String riderName;
    private RiderStatus riderStatus;
    private int currentVolumeLoad;
    private final int maxLoad;
    private final int reliabilityRating;
    private final boolean canHandleFragile;

    public Rider(String riderId, String riderName, int reliabilityRating, boolean canHandleFragile) {
        this.riderId = riderId;
        this.riderName = riderName;
        this.maxLoad = 100;
        this.reliabilityRating = reliabilityRating;
        this.canHandleFragile = canHandleFragile;
        this.currentVolumeLoad = 0;
        this.riderStatus = RiderStatus.AVAILABLE;
    }

    public String getRiderId() {
        return riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public RiderStatus getRiderStatus() {
        return riderStatus;
    }

    public void setRiderStatus(RiderStatus riderStatus) {
        this.riderStatus = riderStatus;
    }

    public int getCurrentVolumeLoad() {
        return currentVolumeLoad;
    }

    public void setCurrentVolumeLoad(int currentVolumeLoad) {
        this.currentVolumeLoad = currentVolumeLoad;
    }

    public int getMaxLoad() {
        return maxLoad;
    }

    public double getReliabilityRating() {
        return reliabilityRating;
    }

    public boolean isCanHandleFragile() {
        return canHandleFragile;
    }
}
