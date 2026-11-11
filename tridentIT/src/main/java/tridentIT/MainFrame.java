package tridentIT;

//============================================================
//MainFrame.java
//Purpose: Creates the main application window
//============================================================

//-------------------------
//Import Statements
//-------------------------

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

//-------------------------
//MainFrame Class
//-------------------------

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 156223475857472485L;

	// -------------------------
	// GUI Variables
	// -------------------------

	private CardLayout cardLayout;

	private JPanel contentPanel;

	private final DatabaseManager databaseManager;

	// -------------------------
	// MainFrame Constructor
	// -------------------------

	public MainFrame() {

		// -------------------------
		// Window Settings
		// -------------------------

		setTitle("Trident Management");

		setSize(1400, 850);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLocationRelativeTo(null);

		// -------------------------
		// Main Window Layout
		// -------------------------

		setLayout(new BorderLayout());

		// -------------------------
		// Create Card Layout
		// -------------------------

		cardLayout = new CardLayout();

		contentPanel = new JPanel(cardLayout);

		// Create the database manager to access tables
		databaseManager = new DatabaseManager();

		// Populate with test data
		// ONLY FOR TESTING!!! REMOVE FOR PRODUCTION!!!
		databaseManager.populateTest();

		// -------------------------
		// Create Application Pages
		// -------------------------

		contentPanel.add(new DashboardPanel(), "dashboard");

		contentPanel.add(new EmployeesPanel(), "employees");

		contentPanel.add(new ActiveEquipmentPanel(), "active");

		contentPanel.add(new AvailableEquipmentPanel(databaseManager), "available");

		contentPanel.add(new ScanEquipmentPanel(), "scan");

		contentPanel.add(new SettingsPanel(), "settings");

		// -------------------------
		// Create Sidebar
		// -------------------------

		Sidebar sidebar = new Sidebar(cardLayout, contentPanel);

		// -------------------------
		// Add Components to Window
		// -------------------------

		add(sidebar, BorderLayout.WEST);

		add(contentPanel, BorderLayout.CENTER);

		// -------------------------
		// Set Starting Page
		// -------------------------

		cardLayout.show(contentPanel, "dashboard");
	}
}
