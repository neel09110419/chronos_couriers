package com.chronos.couriers.model.packageinfo;

public class Package {
    private final String id;
    private final String receiverName;
    private final String receiverAddress;
    private final PackagePriorityType priority;
    private final long orderTime;
    private final long deadline;
    private final boolean fragile;
    private PackageStatus status;
    private String assignedRiderId;
    private final int volume;
    private long pickupTime;
    private long deliveryTime;
    private PackagePaymentStatus packagePaymentStatus;

    public Package(String id, String receiverName, String receiverAddress, PackagePriorityType priority, long orderTime, long deadline, boolean fragile, int volume, PackagePaymentStatus packagePaymentStatus) {
        this.id = id;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.priority = priority;
        this.orderTime = orderTime;
        this.deadline = deadline;
        this.volume = volume;
        this.status = PackageStatus.PENDING;
        this.fragile = fragile;
        this.packagePaymentStatus = packagePaymentStatus;
    }

    public String getId() {
        return id;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
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

    public long getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(long pickupTime) {
        this.pickupTime = pickupTime;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public PackagePaymentStatus getPackagePaymentStatus() {
        return packagePaymentStatus;
    }

    public void setPackagePaymentStatus(PackagePaymentStatus packagePaymentStatus) {
        this.packagePaymentStatus = packagePaymentStatus;
    }
}
