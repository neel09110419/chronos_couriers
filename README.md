# Chronos Couriers

Chronos Couriers is a Java-based command-line courier dispatch simulation system. It enables logistics-like management of package orders and delivery riders with intelligent assignment based on priority, deadline, volume, and fragile item constraints.

---

## Core Features

- Create Riders with:
  - ID, Name
  - Fragile item handling capability
  - Reliability rating (0–5)
  - Load limit (max 100 volume)

- Create Packages with:
  - EXPRESS or STANDARD priority
  - Fragile/Non-Fragile flag
  - Prepaid or COD payment type
  - Auto-generated order and deadline times
  - Receiver’s name and address (visible only after assignment)

- Smart Rider Assignment:
  - Uses priority queue for EXPRESS → STANDARD
  - Assigns based on deadline, priority, and volume
  - Avoids misuse of fragile-capable riders
  - Supports partial loads and multiple assignments

- Rider Operations:
  - View assigned packages per Rider
  - Update package delivery status (Delivered/Cancelled)
  - COD validation (Paid/Not Paid)

---

## Prerequisites

- Java 17 or higher
- No external dependencies (pure Java project)
- CLI-based interaction

---

## How to Run the Application

### Step 1: Clone the Repository

```bash
git clone https://github.com/neel09110419/chronos_couriers.git
cd chronos-couriers

### Step 2: If using a terminal:

javac -d out src/com/chronos/couriers/**/*.java
java -cp out com.chronos.couriers.ChronosCouriersApplication


## You can also run it via any Java IDE like IntelliJ IDEA or Eclipse by running ChronosCouriersApplication.java

## Once CLI Starts you will see

--- Chronos Couriers CLI ---
1. Create Rider
2. Create Order
3. View All Packages
4. Assign Packages to Riders
5. View Packages By RiderId
6. Update Package Delivery Status
7. View Missed EXPRESS Deliveries
8. Exit
Enter your choice:

1. Create Rider
You will be prompted to enter:

Rider ID
Rider Name
Reliability Rating (0–5)
Can Handle Fragile Items (Y/N)

2. Create Order
You will be prompted to enter:

Package ID
Receiver's Name
Receiver's Address
Priority (EXPRESS/STANDARD)
Volume (1–100)
Is Fragile (Y/N)
Payment Status (PREPAID/COD)

3. View All Packages
Displays all packages in the system

4. Assign Packages to Riders
Auto-assigns top-priority packages to eligible riders based on:

Available volume
Fragile item compatibility
Rider reliability

5. View Packages By Rider ID
Enter Rider ID to view:

Rider details
All assigned packages 

6. Update Package Delivery Status
Select a rider -> Choose a package assigned -> Update status:

If PREPAID: Mark as DELIVERED or CANCELLED directly
If COD: Choose if customer paid or not
	If paid: Mark DELIVERED
	If not paid: Mark CANCELLED

7. View Missed EXPRESS Deliveries

