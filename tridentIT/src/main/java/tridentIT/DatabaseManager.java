package tridentIT;

/// ============================================================
/// DatabaseManager.java
/// Purpose: Create and manage connection to local H2 database, including all CRUD statements 
/// ============================================================


// Import statements
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseManager {
	// Query to create equipment DB
	private static final String creationQuery = """
			create table if not exists EQUIPMENT (
			    id number primary key,
			    serial varchar,
			    manu varchar,
			    desc varchar,
			    type varchar,
			    condition varchar,
			    date_added date
			    )
			""";

	// Query to delete Equipment DB
	// KEEP ONLY FOR TESTING PURPOSES!!!!
	private static final String deletionQuery = """
			drop table if exists EQUIPMENT
			""";

	// Query to insert into Equipment DB
	private static final String query = """
			    INSERT INTO EQUIPMENT (id, serial, manu, desc, type, condition, date_added)
			           VALUES (?, ?, ?, ?, ?, ?, ?)
			""";

	// Query to get all equipment in the DB
	private static final String queryAll = """
			    SELECT * FROM EQUIPMENT
			""";

	public DatabaseManager() {
		// Establish connection, then create table
		try (Connection conn = getConnection()) {
			Statement statement = conn.createStatement(); // Prepare statement for querying
			statement.execute(deletionQuery); // Delete existing table FOR TESTING PURPOSES ONLY
			statement.execute(creationQuery); // Create Equipment table if it doesn't exist
			System.out.println("Successfully intitialized database tables.");
		} catch (SQLException e) {
			System.out.println("Database initialization failed.");
		}
	}

	// Create a DB connection
	public Connection getConnection() {
		try {
			var url = "jdbc:h2:./TridentDB;AUTO_SERVER=TRUE"; // Connection URL
			var username = "Admin"; // DB username and password
			var password = "Admin";
			return DriverManager.getConnection(url, username, password); // Return established connection
		} catch (SQLException e) {
			throw new RuntimeException("Failed to connect to database", e);
		}
	}

	// Add passed Equipment object to the Equipment table
	public void addEquip(Equipment equipment) {
		// Establish connection to DB
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(query); // Prepare statement for inserting values
			st.setInt(1, equipment.id()); // Insert values into the prepared statement
			st.setString(2, equipment.serial());
			st.setString(3, equipment.manu());
			st.setString(4, equipment.desc());
			st.setString(5, equipment.type());
			st.setString(6, equipment.condition());
			st.setDate(7, equipment.dateAdded());
			st.executeUpdate(); // Insert equipment to the table
			System.out.println("Successfully saved equipment to database");
		} catch (SQLException e) {
			System.out.println("Failed to save equipment to database.");
		}
	}

	// Get all equipment from the Equipment table
	public List<Equipment> getAllEquipment() {
		// Establish connection
		try (var conn = getConnection()) {
			PreparedStatement st = conn.prepareStatement(queryAll); // Prepare statement to select all equipment
			ResultSet rs = st.executeQuery(); // Execute query, store all equipment
			List<Equipment> equipment = new ArrayList<Equipment>(); // Store all equipment in a list
			while (rs.next()) {
				// Save each equipment item as its own object
				equipment.add(new Equipment(rs.getInt("id"), rs.getString("serial"), rs.getString("manu"),
						rs.getString("desc"), rs.getString("type"), rs.getString("condition"),
						rs.getDate("date_added")));
			}
			return equipment; // Return the full list of equipment
		} catch (SQLException e) {
			System.out.println("Failed to query all equipment from database.");
			return Collections.emptyList(); // If query fails, return empty list to avoid crash
		}
	}

	// Populate the DB with test data
	// FOR TESTING PURPOSES ONLY
	public void populateTest() {
		Equipment[] testData = {

				new Equipment(131, "SN123", "Dell", "Dell Latitude 7430", "Laptop", "Good", Date.valueOf("2026-8-25")),

				new Equipment(142, "SN234", "Apple", "iPad 11", "Tablet", "New", Date.valueOf("2026-8-24")),

				new Equipment(157, "SN345", "HP", "HP E24i G4 WUXGA", "Monitor", "Good", Date.valueOf("2026-8-23")),

				new Equipment(168, "SN456", "Dell", "Dell wireless mouse", "Accessory", "Good",
						Date.valueOf("2026-08-22")),

				new Equipment(172, "SN567", "Lenovo", "Lenovo Thinkpad dock", "Accessory", "Excellent",
						Date.valueOf("2026-8-20")) };

		for (Equipment equip : testData) {
			addEquip(equip);
		}
	}
}
