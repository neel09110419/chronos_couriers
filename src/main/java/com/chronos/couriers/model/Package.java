package com.chronos.couriers.model;

public class Package {
    private final String id;
    private final PackagePriorityType priority;
    private final long orderTime;
    private final long deadline;
    private final boolean fragile;
    private PackageStatus status;
    private String assignedRiderId;
    private final int volume;

    public Package(String id, PackagePriorityType priority, long orderTime, long deadline, boolean fragile, int volume) {
        this.id = id;
        this.priority = priority;
        this.orderTime = orderTime;
        this.deadline = deadline;
        this.volume = volume;
        this.status = PackageStatus.PENDING;
        this.fragile = fragile;
    }

    public String getId() {
        return id;
    }

    public PackagePriorityType getPriority() {
        return priority;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public long getDeadline() {
        return deadline;
    }

    public boolean isFragile() {
        return fragile;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public String getAssignedRiderId() {
        return assignedRiderId;
    }

    public void setAssignedRiderId(String assignedRiderId) {
        this.assignedRiderId = assignedRiderId;
    }

    public int getVolume() {
        return volume;
    }
}
