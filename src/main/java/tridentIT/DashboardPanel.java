package tridentIT;

//============================================================
//DashboardPanel.java
//Purpose: Displays overview of equipment and relevant alerts
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

//-------------------------
//Dashboard Panel
//-------------------------

public class DashboardPanel extends JPanel {

	private static final long serialVersionUID = 786085339223783534L;

	// -------------------------
	// Constructor
	// -------------------------

	public DashboardPanel() {

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

		JLabel title = new JLabel("Dashboard");

		// -------------------------
		// Title Font
		// -------------------------

		title.setFont(new Font("SansSerif", Font.BOLD, 28));

		// -------------------------
		// Add Title
		// -------------------------

		add(title, BorderLayout.NORTH);
	}
}
