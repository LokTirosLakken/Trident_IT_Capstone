package tridentIT;

//============================================================

//ActiveEquipmentPanel.java
//Purpose: Displays currently assigned equipment
//============================================================

//-------------------------
//Import Statements
//-------------------------

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

//-------------------------
//Active Equipment Panel
//-------------------------

public class ActiveEquipmentPanel extends JPanel {

	private static final long serialVersionUID = 8061989651379950343L;

	// -------------------------
	// Constructor
	// -------------------------

	public ActiveEquipmentPanel() {

		// -------------------------
		// Background
		// -------------------------

		setBackground(new Color(245, 247, 250));

		// -------------------------
		// Layout
		// -------------------------

		setLayout(new BorderLayout());

		// -------------------------
		// Padding
		// -------------------------

		setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

		// -------------------------
		// Page Title
		// -------------------------

		JLabel title = new JLabel("Active Equipment");

		// -------------------------
		// Title Font
		// -------------------------

		title.setFont(new Font("SansSerif", Font.BOLD, 28));

		// -------------------------
		// Add Title
		// -------------------------

		add(title, BorderLayout.NORTH);
		
		// =====================================================
				// EQUIPMENT TABLE
				// =====================================================

				// -------------------------
				// Table Column Names
				// -------------------------

				String[] columns = {

						"Asset ID", "Equipment", "Type", "Condition", "Date Added" };

				// -------------------------
				// Temporary Table Data
				// -------------------------

				Object[][] data = {

						{ "EQP-00131", "Dell Latitude 7430", "Laptop", "Good", "Aug 25, 2026" },

						{ "EQP-00142", "iPad 11", "Tablet", "New", "Aug 24, 2026" },

						{ "EQP-00157", "HP Monitor 24", "Monitor", "Good", "Aug 23, 2026" },

						{ "EQP-00168", "Dell Wireless Mouse", "Accessory", "Good", "Aug 22, 2026" },

						{ "EQP-00172", "Lenovo ThinkPad Dock", "Accessory", "Excellent", "Aug 20, 2026" } };

				// -------------------------
				// Create Table
				// -------------------------

				JTable table = new JTable(data, columns);

				// -------------------------
				// Table Row Height
				// -------------------------

				table.setRowHeight(55);

				// -------------------------
				// Table Font
				// -------------------------

				table.setFont(new Font("SansSerif", Font.PLAIN, 14));

				// -------------------------
				// Header Font
				// -------------------------

				table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

				// -------------------------
				// Header Height
				// -------------------------

				table.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));

				// -------------------------
				// Remove Grid
				// -------------------------

				table.setShowGrid(false);

				// -------------------------
				// Selection Color
				// -------------------------

				table.setSelectionBackground(new Color(220, 235, 255));

				// -------------------------
				// Scroll Pane
				// -------------------------

				JScrollPane scrollPane = new JScrollPane(table);

				scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

				// =====================================================
				// CENTER PANEL
				// =====================================================

				JPanel centerPanel = new JPanel(new BorderLayout(0, 15));

				centerPanel.setOpaque(false);

				// -------------------------
				// Add Table
				// -------------------------

				centerPanel.add(scrollPane, BorderLayout.CENTER);

				// =====================================================
				// ADD COMPONENTS TO PAGE
				// =====================================================

				add(centerPanel, BorderLayout.CENTER);
	}
}
