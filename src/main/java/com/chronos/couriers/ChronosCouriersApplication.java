package com.chronos.couriers;

import com.chronos.couriers.model.PackagePriorityType;
import com.chronos.couriers.service.CreatePackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ChronosCouriersApplication {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		CreatePackage createPackage = new CreatePackage();

		try {
			System.out.print("Enter Package ID: ");
			String id = scanner.nextLine();

			System.out.print("Enter Priority (EXPRESS/STANDARD): ");
			PackagePriorityType priority = PackagePriorityType.valueOf(scanner.nextLine().toUpperCase());

			System.out.print("Is Fragile? (true/false): ");
			boolean fragile = Boolean.parseBoolean(scanner.nextLine());

			createPackage.placeOrder(id, priority, fragile);

		} catch (CreatePackage.PackageAlreadyExistsException |
				 CreatePackage.InvalidPackageDataException e) {
			System.err.println("Error placing package: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Unexpected error: " + e.getMessage());
		}
	}
}
