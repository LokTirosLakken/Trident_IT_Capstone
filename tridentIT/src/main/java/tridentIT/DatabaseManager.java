package tridentIT;

import java.sql.Connection;
import java.sql.Date;
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
			    condition varchar,
			    date_added date
			    )
			""";

	private static final String deletionQuery = """
			drop table if exists EQUIPMENT
			""";

	private static final String query = """
			    INSERT INTO EQUIPMENT (id, serial, manu, desc, type, condition, date_added)
			           VALUES (?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String queryAll = """
			    SELECT * FROM EQUIPMENT
			""";

	public DatabaseManager() {
		// Initialize database tables if they don't exist
		try (Connection conn = getConnection()) {
			Statement statement = conn.createStatement();
			statement.execute(deletionQuery);
			statement.execute(creationQuery);
			System.out.println("Successfully intitialized database tables.");
		} catch (SQLException e) {
			System.out.println("Database initialization failed.");
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
			st.setDate(7, equipment.dateAdded());
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
				equipment.add(new Equipment(rs.getInt("id"), rs.getString("serial"), rs.getString("manu"),
						rs.getString("desc"), rs.getString("type"), rs.getString("condition"),
						rs.getDate("date_added")));
			}
			return equipment;
		} catch (SQLException e) {
			System.out.println("Failed to query all equipment from database.");
			return Collections.emptyList();
		}
	}

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
