package tridentIT;

/// ============================================================
/// DatabaseManager.java
/// Purpose: Create and manage connection to local H2 database, including all CRUD statements 
/// ============================================================

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
	// Query to create equipment table
	private static final String equipmentCreationQuery = """
			create table if not exists EQUIPMENT (
			    equip_id number,
			    serial varchar,
			    manu varchar,
			    desc varchar,
			    type varchar,
			    condition varchar,
			    date_added date,
			    primary key(equip_id)
			    )
			""";

	// Query to create maintenance table
	private static final String maintCreationQuery = """
			create table if not exists MAINTENANCE (
				equip_id number,
				maint_start date,
				maint_end date,
				notes varchar,
				primary key (equip_id, maint_start),
				constraint fk_maint_equipID
				foreign key (equip_id)
				references EQUIPMENT(equip_id)
				)
			""";

	// Query to create employee table
	private static final String empCreationQuery = """
			create table if not exists EMPLOYEE (
				emp_id number,
				name varchar,
				dept varchar,
				email varchar,
				primary key (emp_id)
			)
			""";

	// Query to create history table
	private static final String historyCreationQuery = """
			create table if not exists HISTORY (
				equip_id number,
				emp_id number,
				checkout date,
				checkin date,
				primary key (equip_id, emp_id, checkout),
				constraint fk_history_equipID
				foreign key (equip_id)
				references EQUIPMENT(equip_id),
				constraint fk_history_empID
				foreign key (emp_ID)
				references EMPLOYEE(emp_id)
			)
			""";

	// Query to delete Equipment DB
	// KEEP ONLY FOR TESTING PURPOSES!!!!
	@SuppressWarnings("unused")
	private static final String deletionQuery = """
			drop table if exists EQUIPMENT, MAINTENANCE, EMPLOYEE, HISTORY
			""";

	// Query to insert into Equipment table
	private static final String insertEquipQuery = """
			    INSERT INTO EQUIPMENT (equip_id, serial, manu, desc, type, condition, date_added)
			           VALUES (?, ?, ?, ?, ?, ?, ?)
			""";

	// Query to insert into Maintenance table
	private static final String insertMaintQuery = """
			    INSERT INTO MAINTENANCE (equip_id, maint_start, notes)
				           VALUES (?, ?, ?)
			""";

	// Query to update the notes field of a maintenance record
	private static final String updateMaintNotesQuery = """
				UPDATE MAINTENANCE
				SET notes = ?
				WHERE equip_id = ?
				AND maint_start = ?
			""";

	// Query to update the end date of a maintenance record
	private static final String updateMaintEndQuery = """
				UPDATE MAINTENANCE
				SET maint_end = ?
				WHERE equip_id = ?
				AND maint_start = ?
			""";

	// Query to insert into Employee table
	private static final String insertEmpQuery = """
			    INSERT INTO EMPLOYEE (emp_id, name, dept, email)
			           VALUES (?, ?, ?, ?)
			""";

	private static final String updateEmployeeQuery = """
			UPDATE EMPLOYEE SET name = ?, dept = ?, email = ?
			WHERE emp_id = ?
			""";

	private static final String deleteEmployeeQuery = """
			DELETE FROM EMPLOYEE WHERE emp_id = ?
			""";

	// Query to insert into History table
	private static final String insertHistQuery = """
				insert into HISTORY (equip_id, emp_id, checkout)
					VALUES (?, ?, ?)
			""";

	// Query to update checkin date of a history record
	private static final String updateHistCheckinQuery = """
				UPDATE HISTORY
				SET checkin = ?
				WHERE equip_id = ?
				AND emp_id = ?
				and checkout = ?
			""";

	// Query to get all equipment in the Equipment table
	private static final String queryAllEquip = """
			    SELECT * FROM EQUIPMENT ORDER BY equip_id
			""";

	// Query to get all maintenance records in the Maintenance table
	private static final String queryAllMaint = """
			    SELECT * FROM MAINTENANCE ORDER BY maint_start
			""";

	// Query to get all currently active maintenance records
	private static final String queryAllActiveMaint = """
			    SELECT * FROM MAINTENANCE
			    WHERE maint_end IS NULL
			    ORDER BY maint_start
			""";

	// Query to get all employees in the Employee table
	private static final String queryAllEmp = """
			    SELECT * FROM EMPLOYEE ORDER BY emp_id
			""";

	private static final String employeeExistsQuery = """
			SELECT COUNT(*) FROM EMPLOYEE WHERE UPPER(emp_id) = UPPER(?)
			""";

	// Query to get all history records in the History table
	private static final String queryAllHist = """
				    SELECT * FROM HISTORY
			""";

	// Query to get all active equipment
	// If an item has not been checked back in, assume it is active
	private static final String queryAllActive = """
				SELECT * FROM EQUIPMENT
				WHERE equip_id IN (
					SELECT equip_id FROM MAINTENANCE
					WHERE maint_end IS NULL)
				ORDER BY equip_id
			""";

	// Query to get all available equipment
	private static final String queryAllAvailable = """
				SELECT * FROM EQUIPMENT
				WHERE equip_id NOT IN (
					SELECT equip_id FROM HISTORY
					WHERE CHECKIN IS NULL)
				AND equip_id NOT IN (
					SELECT equip_id FROM MAINTENANCE
					WHERE maint_end IS NULL)
				ORDER BY equip_id
			""";

	// Construct the DB manager
	public DatabaseManager() {
		// Establish connection, then create table
		try (Connection conn = getConnection()) {
			Statement statement = conn.createStatement(); // Prepare statement for querying
			// statement.execute(deletionQuery); // Delete existing table FOR TESTING
			// PURPOSES ONLY
			statement.execute(equipmentCreationQuery); // Create Equipment table
			statement.execute(maintCreationQuery); // Create Maintenance table
			statement.execute(empCreationQuery); // Create Employee table
			statement.execute(historyCreationQuery); // Create Employee table
			// System.out.println("Successfully intitialized database tables.");
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
	public void addEquip(int equip_id, String serial, String manu, String desc, String type, String condition,
			Date dateAdded) {
		// Establish connection to DB
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(insertEquipQuery); // Prepare statement for inserting values
			st.setInt(1, equip_id); // Insert values into the prepared statement
			st.setString(2, serial);
			st.setString(3, manu);
			st.setString(4, desc);
			st.setString(5, type);
			st.setString(6, condition);
			st.setDate(7, dateAdded);
			st.executeUpdate(); // Insert equipment to the table

			// System.out.println("Successfully saved equipment to database");
		} catch (SQLException e) {
			System.out.println("Failed to save equipment to database.");
		}
	}

	// Add passed maintenance record value to the Maintenance table
	public void addMaint(int equip_id, Date maint_start, String notes) {
		// Establish connection to DB
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(insertMaintQuery); // Prepare statement for inserting values
			st.setInt(1, equip_id); // Insert values into the prepared statement
			st.setDate(2, maint_start);
			st.setString(3, notes);
			st.executeUpdate(); // Insert maintenance record to the table
			// System.out.println("Successfully saved mainteance record to database.");
		} catch (SQLException e) {
			System.out.println("Failed to save maintenance record to database.");
		}
	}

	// Update the notes field for a given maintenance record.
	public void updateMaintNotes(int equip_id, Date maint_start, String notes) {
		// Establish connection to DB
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(updateMaintNotesQuery); // Prepare statement for updating value
			st.setString(1, notes); // Insert values into the prepared statement
			st.setInt(2, equip_id);
			st.setDate(3, maint_start);
			st.executeUpdate(); // Update note
			// System.out.println("Successfully updated maintenance note.");
		} catch (SQLException e) {
			System.out.println("Failed to update maintenance note.");
		}
	}

	// Update the maintenance end date field for a given maintenance record.
	public void updateMaintEnd(int equip_id, Date maint_start, Date maint_end) {
		// Establish connection to DB
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(updateMaintEndQuery); // Prepare statement for updating value
			st.setDate(1, maint_end); // Insert values into the prepared statement
			st.setInt(2, equip_id);
			st.setDate(3, maint_start);
			st.executeUpdate(); // Update note
			// System.out.println("Successfully updated maintenance note.");
		} catch (SQLException e) {
			System.out.println("Failed to update maintenance note.");
		}
	}

	// Add passed employee to the Employee table
	public boolean addEmp(int emp_id, String name, String dept, String email) {
		// Establish connection to DB
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(insertEmpQuery); // Prepare statement for inserting values
			st.setInt(1, emp_id); // Insert values into the prepared statement
			st.setString(2, name);
			st.setString(3, dept);
			st.setString(4, email);
			st.executeUpdate(); // Insert employee to the table
			// System.out.println("Successfully saved employee to database.");
			return true;
		} catch (SQLException e) {
			System.out.println("Failed to save employee to database.");
			return false;
		}
	}

	public boolean updateEmployee(int emp_id, String name, String dept, String email) {
		try (var conn = getConnection(); var st = conn.prepareStatement(updateEmployeeQuery)) {
			st.setString(1, name);
			st.setString(2, dept);
			st.setString(3, email);
			st.setInt(4, emp_id);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("Failed to update employee in database.");
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteEmployee(int id) {
		try (var conn = getConnection(); var st = conn.prepareStatement(deleteEmployeeQuery)) {
			st.setInt(1, id);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("Failed to delete employee from database.");
			e.printStackTrace();
			return false;
		}
	}

	// Add passed history record to the History table
	public void addHist(int equip_id, int emp_id, Date checkout) {
		// Establish connection to DB
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(insertHistQuery); // Prepare statement for inserting values
			st.setInt(1, equip_id); // Insert values into the prepared statement
			st.setInt(2, emp_id);
			st.setDate(3, checkout);
			st.executeUpdate(); // Insert history record to the table
			// System.out.println("Successfully saved equipment history to database.");
		} catch (SQLException e) {
			System.out.println("Failed to save equipment history to database.");
		}
	}

	// Update the History checkin field for a given history record.
	public void updateHistCheckin(int equip_id, int emp_id, Date checkout, Date checkin) {
		// Establish connection to DB
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(updateHistCheckinQuery); // Prepare statement for updating value
			st.setDate(1, checkin); // Insert values into the prepared statement
			st.setInt(2, equip_id);
			st.setInt(3, emp_id);
			st.setDate(4, checkout);
			st.executeUpdate(); // Update note
			// System.out.println("Successfully updated History checkin.");
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Failed to update history checkin.");
		}
	}

	// Get all equipment from the Equipment table
	public List<List<Object>> getAllEquipment() {
		// Establish connection
		try (var conn = getConnection()) {
			PreparedStatement st = conn.prepareStatement(queryAllEquip); // Prepare statement to select all equipment
			ResultSet rs = st.executeQuery(); // Execute query, store all equipment
			List<List<Object>> result = new ArrayList<List<Object>>(); // Store all equipment in a list
			while (rs.next()) {
				List<Object> equipment = new ArrayList<Object>();
				equipment.add(rs.getInt("equip_id"));
				equipment.add(rs.getString("serial"));
				equipment.add(rs.getString("manu"));
				equipment.add(rs.getString("desc"));
				equipment.add(rs.getString("type"));
				equipment.add(rs.getString("condition"));

				result.add(equipment);
			}
			return result; // Return the full list of equipment
		} catch (SQLException e) {
			System.out.println("Failed to query all equipment from database.");
			return Collections.emptyList(); // If query fails, return empty list to avoid crash
		}
	}

	// Get all maintenance records from the Maintenance table
	public List<List<Object>> getAllMaint() {

		try (var conn = getConnection()) {
			PreparedStatement st = conn.prepareStatement(queryAllMaint); // Prepare statement to select all records
			ResultSet rs = st.executeQuery(); // Execute query, store all records

			List<List<Object>> result = new ArrayList<List<Object>>();
			while (rs.next()) {
				List<Object> maintRecord = new ArrayList<Object>();
				maintRecord.add(rs.getInt("equip_id"));
				maintRecord.add(rs.getDate("maint_start"));
				maintRecord.add(rs.getDate("maint_end"));
				maintRecord.add(rs.getString("notes"));

				result.add(maintRecord);
			}

			return result; // Return all records
		} catch (SQLException e) {
			System.out.println("Failed to query all maintenance records from database.");
			return null;
		}
	}

	// Get all maintenance records from the Maintenance table
	public List<List<Object>> getAllActiveMaint() {

		try (var conn = getConnection()) {
			PreparedStatement st = conn.prepareStatement(queryAllActiveMaint); // Prepare statement to select all
																				// records
			ResultSet rs = st.executeQuery(); // Execute query, store all records

			List<List<Object>> result = new ArrayList<List<Object>>();
			while (rs.next()) {
				List<Object> maintRecord = new ArrayList<Object>();
				maintRecord.add(rs.getInt("equip_id"));
				maintRecord.add(rs.getDate("maint_start"));
				maintRecord.add(rs.getDate("maint_end"));
				maintRecord.add(rs.getString("notes"));

				result.add(maintRecord);
			}

			return result; // Return all records
		} catch (SQLException e) {
			System.out.println("Failed to query all maintenance records from database.");
			return null;
		}
	}

	// Get all employees from the Employee table
	public List<List<Object>> getAllEmp() {
		try (var conn = getConnection()) {
			PreparedStatement st = conn.prepareStatement(queryAllEmp); // Prepare statement to select all employees
			ResultSet rs = st.executeQuery(); // Execute query, store all employees

			List<List<Object>> result = new ArrayList<List<Object>>();
			while (rs.next()) {
				List<Object> employee = new ArrayList<Object>();
				employee.add(rs.getInt("emp_id"));
				employee.add(rs.getString("name"));
				employee.add(rs.getString("dept"));
				employee.add(rs.getString("email"));

				result.add(employee);
			}

			return result; // Return all records
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Failed to query all employees from database.");
			return null;
		}
	}

	public boolean employeeExists(String id) {
		try (var conn = getConnection(); var st = conn.prepareStatement(employeeExistsQuery)) {
			st.setString(1, id);
			try (var rs = st.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Get all history records from the History table
	public ResultSet getAllHist() {
		try (var conn = getConnection()) {
			PreparedStatement st = conn.prepareStatement(queryAllHist); // Prepare statement to select all history
																		// records
			ResultSet rs = st.executeQuery(); // Execute query, store all history records

			return rs; // Return all history records
		} catch (SQLException e) {
			System.out.println("Failed to query all history records from database.");
			return null;
		}
	}

	// Get all equipment currently assigned to an employee
	public List<List<Object>> getAllActive() {
		try (var conn = getConnection()) {
			PreparedStatement st = conn.prepareStatement(queryAllActive); // Prepare statement to select all active
			ResultSet rs = st.executeQuery(); // Execute query, store all active equipment

			List<List<Object>> result = new ArrayList<List<Object>>();
			while (rs.next()) {
				List<Object> active = new ArrayList<Object>();
				active.add(rs.getInt("equip_id"));
				active.add(rs.getString("serial"));
				active.add(rs.getString("manu"));
				active.add(rs.getString("desc"));
				active.add(rs.getString("type"));
				active.add(rs.getString("condition"));
				active.add(rs.getDate("date_added"));

				result.add(active);
			}

			return result; // Return all active equipment
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Failed to query all active equipment from database.");
			return null;
		}
	}

	public List<List<Object>> getAllAvailable() {
		try (var conn = getConnection()) {
			PreparedStatement st = conn.prepareStatement(queryAllAvailable); // Prepare statement to select all active
																				// formatting
			ResultSet rs = st.executeQuery(); // Execute query, store all active equipment

			List<List<Object>> result = new ArrayList<List<Object>>();
			while (rs.next()) {
				List<Object> available = new ArrayList<Object>();
				available.add(rs.getInt("equip_id"));
				available.add(rs.getString("desc"));
				available.add(rs.getString("type"));
				available.add(rs.getString("condition"));
				available.add(rs.getDate("date_added"));

				result.add(available);
			}

			return result; // Return all active equipment
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Failed to query all available equipment from database.");
			return null;
		}
	}

	// Populate the DB with test data
	// FOR TESTING PURPOSES ONLY
	public void populateTest() {
		// Create example equipment
		Object[][] testEquip = {
				{ 131, "SN123", "Dell", "Dell Latitude 7430", "Laptop", "Damaged", Date.valueOf("2026-8-25") },
				{ 142, "SN234", "Apple", "iPad 11", "Tablet", "New", Date.valueOf("2026-8-24") },
				{ 157, "SN345", "HP", "HP E24i G4 WUXGA", "Monitor", "Good", Date.valueOf("2026-8-23") },
				{ 168, "SN456", "Dell", "Dell wireless mouse", "Accessory", "Good", Date.valueOf("2026-08-22") },
				{ 172, "SN567", "Lenovo", "Lenovo Thinkpad dock", "Accessory", "Damaged", Date.valueOf("2026-8-20") },
				{ 104, "SN678", "Apple", "MacBook Pro 16\"", "Laptop", "Good", Date.valueOf("2026-8-20") },
				{ 119, "SN789", "Dell", "Dell Latitude 7430", "Laptop", "Excellent", Date.valueOf("2026-8-21") },
				{ 138, "SN890", "HP", "HP Monitor 27\"", "Monitor", "Poor", Date.valueOf("2026-8-22") } };

		// Create example maintenance records.
		Object[][] testMaint = { { 131, Date.valueOf("2026-8-25"), "Broken display. Awaiting parts." },
				{ 172, Date.valueOf("2026-8-20"), "Damaged charging port, awaiting teardown to establish severity." },
				{ 157, Date.valueOf("2026-8-20"), "Refuses to turn on. Damaged power cord suspected." } };

		// Create example employees
		Object[][] testEmp = { { 1001, "Sarah Jenkins", "Marketing", "s.jenkins@company.com" },
				{ 1002, "Marcus Vance", "Operations", "m.vance@company.com" },
				{ 1003, "Elena Rostova", "Finance", "e.rostova@company.com" },
				{ 1004, "David Kim", "Engineering", "d.kim@company.com" },
				{ 1024, "John Smith", "IT", "john.smith@company.com" },
				{ 1025, "Sarah Jones", "HR", "sarah.jones@company.com" },
				{ 1026, "Mike Brown", "Finance", "mike.brown@company.com" },
				{ 1027, "Emily Davis", "Marketing", "emily.davis@company.com" },
				{ 1028, "David Wilson", "Operations", "david.wilson@company.com" } };

		// Create example equipment histories
		Object[][] testHist = { { 104, 1001, Date.valueOf("2026-8-20") }, { 119, 1002, Date.valueOf("2026-8-21") },
				{ 138, 1003, Date.valueOf("2026-8-22") }, { 142, 1004, Date.valueOf("2026-8-1") } };

		// Add example equipment
		for (Object[] equip : testEquip) {
			int equipID = (int) equip[0];
			String serial = (String) equip[1];
			String manu = (String) equip[2];
			String desc = (String) equip[3];
			String type = (String) equip[4];
			String condition = (String) equip[5];
			Date dateAdded = (Date) equip[6];

			addEquip(equipID, serial, manu, desc, type, condition, dateAdded);
		}

		// Add example maintenance records
		for (Object[] maint : testMaint) {
			int equipID = (int) maint[0];
			Date maintStart = (Date) maint[1];
			String notes = (String) maint[2];
			addMaint(equipID, maintStart, notes);
		}

		// Update on maintenance record to ensure functions work.
		updateMaintNotes(157, Date.valueOf("2026-8-20"),
				"Refused to turn on. Power cord damaged; replaced. No foruther action needed.");
		updateMaintEnd(157, Date.valueOf("2026-8-20"), Date.valueOf("2026-8-23"));

		// Add example employees
		for (Object[] employee : testEmp) {
			int empID = (int) employee[0];
			String empName = (String) employee[1];
			String empDept = (String) employee[2];
			String empEmail = (String) employee[3];
			addEmp(empID, empName, empDept, empEmail);
		}

		// Add example history records
		for (Object[] hist : testHist) {
			int equipID = (int) hist[0];
			int empID = (int) hist[1];
			Date checkout = (Date) hist[2];
			addHist(equipID, empID, checkout);
		}

		// Update on history record to ensure functions work.
		updateHistCheckin(142, 1004, Date.valueOf("2026-8-1"), Date.valueOf("2026-8-24"));

		// Verify test data was added
		System.out.println("All test data added.");
	}
}
