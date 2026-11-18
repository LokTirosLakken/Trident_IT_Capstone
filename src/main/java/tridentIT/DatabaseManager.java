package tridentIT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseManager {
	private static final String creationQuery = """
			create table if not exists EQUIPMENT (
			    id number primary key,
			    serial varchar,
			    manu varchar,
			    desc varchar,
			    type varchar,
			    condition varchar
			    )
			""";

	private static final String createEmployeesQuery = """
			create table if not exists EMPLOYEES (
			    id varchar primary key,
			    name varchar,
			    department varchar,
			    email varchar
			    )
			""";

	private static final String deletionQuery = """
			drop table if exists EQUIPMENT
			""";

	private static final String query = """
			    INSERT INTO EQUIPMENT (id, serial, manu, desc, type, condition)
			           VALUES (?, ?, ?, ?, ?, ?)
			""";

	private static final String queryAll = """
			    SELECT * FROM EQUIPMENT
			""";

	// NEW: employee queries
	private static final String insertEmployeeQuery = """
			INSERT INTO EMPLOYEES (id, name, department, email)
			VALUES (?, ?, ?, ?)
			""";

	private static final String queryAllEmployees = """
			SELECT id, name, department, email FROM EMPLOYEES ORDER BY id
			""";

	private static final String maxEmployeeIdQuery = """
			SELECT MAX(id) FROM EMPLOYEES
			""";

	// NEW: edit and delete queries
	private static final String updateEmployeeQuery = """
			UPDATE EMPLOYEES SET name = ?, department = ?, email = ?
			WHERE id = ?
			""";

	private static final String deleteEmployeeQuery = """
			DELETE FROM EMPLOYEES WHERE id = ?
			""";

	// NEW: checks whether an employee ID is already taken (ignores upper/lower case)
	private static final String employeeExistsQuery = """
			SELECT COUNT(*) FROM EMPLOYEES WHERE UPPER(id) = UPPER(?)
			""";

	public DatabaseManager() {
		try (Connection conn = getConnection();
			Statement statement = conn.createStatement()) {
			statement.execute(creationQuery);
			statement.execute(createEmployeesQuery);
			System.out.println("Successfully initialized database tables.");
		} catch (SQLException e) {
			System.out.println("Database initialization failed.");
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		try {
			var url = "jdbc:h2:./TridentDB;AUTO_SERVER=TRUE";
			var username = "Admin";
			var password = "Admin";
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to connect to database", e);
		}
	}

	public void addEquip(Equipment equipment) {
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(query);
			st.setInt(1, equipment.id());
			st.setString(2, equipment.serial());
			st.setString(3, equipment.manu());
			st.setString(4, equipment.desc());
			st.setString(5, equipment.type());
			st.setString(6, equipment.condition());
			st.executeUpdate();
			System.out.println("Successfully saved equipment to database");
		} catch (SQLException e) {
			System.out.println("Failed to save equipment to database.");
		}
	}

	public List<Equipment> getAllEquipment() {
		try (var conn = getConnection()) {
			var st = conn.prepareStatement(queryAll);
			var rs = st.executeQuery();
			var equipment = new ArrayList<Equipment>();
			while (rs.next()) {
				equipment.add(new Equipment(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6)));
			}
			return equipment;
		} catch (SQLException e) {
			System.out.println("Failed to query all equipment from database.");
			return Collections.emptyList();
		}
	}

	public void populateTest() {
		// If the table already has rows, stop here and don't insert anything
		if (!getAllEquipment().isEmpty()) {
			return;
		}

		Equipment[] testData = {

				new Equipment(131, "SN123", "Dell", "Dell Latitude 7430", "Laptop", "Good"),

				new Equipment(142, "SN234", "Apple", "iPad 11", "Tablet", "New"),

				new Equipment(157, "SN345", "HP", "HP E24i G4 WUXGA", "Monitor", "Good"),

				new Equipment(168, "SN456", "Dell", "Dell wireless mouse", "Accessory", "Good"),

				new Equipment(172, "SN567", "Lenovo", "Lenovo Thinkpad dock", "Accessory", "Excellent") };

		for (Equipment equip : testData) {
			addEquip(equip);
		}
	}

	// =====================================================
	// NEW: Employee methods
	// =====================================================

	public boolean addEmployee(Employee employee) {
		try (var conn = getConnection();
			var st = conn.prepareStatement(insertEmployeeQuery)) {
			st.setString(1, employee.id());
			st.setString(2, employee.name());
			st.setString(3, employee.department());
			st.setString(4, employee.email());
			st.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("Failed to save employee to database.");
			e.printStackTrace();
			return false;
		}
	}

	public List<Employee> getAllEmployees() {
		try (var conn = getConnection();
			var st = conn.prepareStatement(queryAllEmployees);
			var rs = st.executeQuery()) {
			var employees = new ArrayList<Employee>();
			while (rs.next()) {
				employees.add(new Employee(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			}
			return employees;
		} catch (SQLException e) {
			System.out.println("Failed to query employees from database.");
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	// Returns the next free ID like E1029
	public String getNextEmployeeId() {
		try (var conn = getConnection();
			var st = conn.prepareStatement(maxEmployeeIdQuery);
			var rs = st.executeQuery()) {
			if (rs.next() && rs.getString(1) != null) {
				int number = Integer.parseInt(rs.getString(1).substring(1));
				return "E" + (number + 1);
			}
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
		return "E1001";
	}

	// Optional: fills the table with sample employees the first time only
	public void populateTestEmployees() {
		if (!getAllEmployees().isEmpty()) {
			return;
		}
		addEmployee(new Employee("E1024", "John Smith", "IT", "john.smith@company.com"));
		addEmployee(new Employee("E1025", "Sarah Jones", "HR", "sarah.jones@company.com"));
		addEmployee(new Employee("E1026", "Mike Brown", "Finance", "mike.brown@company.com"));
		addEmployee(new Employee("E1027", "Emily Davis", "Marketing", "emily.davis@company.com"));
		addEmployee(new Employee("E1028", "David Wilson", "Operations", "david.wilson@company.com"));
	}

	// NEW: changes the name, department and email of the employee with the same ID
	public boolean updateEmployee(Employee employee) {
		try (var conn = getConnection();
			var st = conn.prepareStatement(updateEmployeeQuery)) {
			st.setString(1, employee.name());
			st.setString(2, employee.department());
			st.setString(3, employee.email());
			st.setString(4, employee.id());
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("Failed to update employee in database.");
			e.printStackTrace();
			return false;
		}
	}

	// NEW: true if an employee with this ID already exists
	public boolean employeeExists(String id) {
		try (var conn = getConnection();
			var st = conn.prepareStatement(employeeExistsQuery)) {
			st.setString(1, id);
			try (var rs = st.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// NEW: removes the employee with this ID
	public boolean deleteEmployee(String id) {
		try (var conn = getConnection();
			var st = conn.prepareStatement(deleteEmployeeQuery)) {
			st.setString(1, id);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("Failed to delete employee from database.");
			e.printStackTrace();
			return false;
		}
	}
}