# Chronos Couriers

Chronos Couriers is a Java-based command-line courier dispatch simulation system. It enables logistics-like management of package orders and delivery riders with intelligent assignment based on priority, deadline, volume, and fragile item constraints.

---

## Design Choices & Algorithms

### Architecture

This CLI-based logistics application follows a clean modular architecture:

* **Model Layer**: Domain classes like `Package`, `Rider`, `PackageStatus`, etc.
* **Service Layer**: Core business logic (e.g., package creation, rider assignment, audit tracking).
* **CLI Interface**: Menu-driven interaction via `ChronosCouriersApplication`.

This separation of concerns improves maintainability, scalability, and testability.

---

### Data Structures

#### Priority Queue

Used to prioritize packages based on:

* Delivery urgency (`EXPRESS` > `STANDARD`)
* Earliest deadlines
* First-in order time

---

### Package Assignment: Greedy Heuristic

* Assigns riders to packages in priority order.
* Chooses available riders with sufficient capacity and, if needed, fragile-handling ability.
* Selects the most reliable eligible rider first.

This is a greedy assignment algorithm—fast, practical, and effective for real-time dispatching.

---

### Audit Logic

* Implemented in `AuditService`.
* Identifies `EXPRESS` packages that were delivered after the deadline.
* Outputs audit logs with timestamps and package IDs.

---

### Input Validation & Error Handling

The application ensures robust user-friendly input validation for:

* Required fields (e.g., IDs, names)
* Enum values (`EXPRESS`, `STANDARD`, `PREPAID`, `COD`)
* Numeric bounds (e.g., volume 1–100, reliability 0–5)

Invalid inputs are caught early with descriptive error messages.

---

### Testing Strategy

* Unit tests for core services (`CreatePackage`, `AssignPackageToRider`, etc.)
* CLI tests using `System.setIn()` and `System.out` stream capture
* Coverage includes valid flows, edge cases, and exception handling

---

## Core Features

* **Create Riders**:

  * ID, name, fragile item handling capability
  * Reliability rating (0–5)
  * Max load capacity (100 volume units)

* **Create Packages**:

  * Priority: `EXPRESS` or `STANDARD`
  * Fragile/non-fragile flag
  * Payment type: `PREPAID` or `COD`
  * Auto-generated order/deadline times
  * Receiver details

* **Smart Assignment Logic**:

  * Priority queue-based dispatching
  * Rider capacity check
  * Fragile item compatibility
  * Reliability-first heuristic

* **Rider Operations**:

  * View all assigned packages
  * Update package delivery status

    * Handles COD payment verification

* **Audit Support**:

  * Displays delayed deliveries of `EXPRESS` packages

---

## Prerequisites

* Java 17 or higher
* No external dependencies (pure Java project)
* CLI-based interaction

---

## How to Run the Application

### 1. Clone the Repository

```bash
git clone https://github.com/neel09110419/chronos_couriers.git
cd chronos_couriers
```

### 2. Compile & Run

#### Option A: Using Terminal

```bash
javac -d out src/com/chronos/couriers/**/*.java
java -cp out com.chronos.couriers.ChronosCouriersApplication
```

#### Option B: Using an IDE

Open the project in IntelliJ IDEA or Eclipse and run `ChronosCouriersApplication.java`.

---

## CLI Menu Overview

When the program starts, you will see:

```
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
```

---

## CLI Menu Breakdown

### 1. Create Rider

You will be prompted to enter:

* Rider ID
* Rider Name
* Reliability Rating (0–5)
* Can Handle Fragile Items? (Y/N)

---

### 2. Create Order

You will be prompted to enter:

* Package ID
* Receiver's Name
* Receiver's Address
* Priority (`EXPRESS` / `STANDARD`)
* Volume (1–100)
* Is Fragile? (Y/N)
* Payment Status (`PREPAID` / `COD`)

---

### 3. View All Packages

Displays all current packages in the system, along with their status, priority, and timestamps.

---

### 4. Assign Packages to Riders

Automatically assigns packages based on:

* Rider availability
* Package volume and fragility
* Delivery priority and deadlines

---

### 5. View Packages By Rider ID

Enter a rider ID to view:

* Rider details
* All packages assigned to them

---

### 6. Update Package Delivery Status

* Select a rider and package.
* If package is `PREPAID`, mark it as `DELIVERED` or `CANCELLED`.
* If package is `COD`, system asks whether the customer paid:

  * If yes → marked as `DELIVERED`
  * If no → marked as `CANCELLED`

---

### 7. View Missed EXPRESS Deliveries

Displays all `EXPRESS` packages that were delivered past their deadlines.

---
