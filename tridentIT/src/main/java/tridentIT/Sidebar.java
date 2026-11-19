package tridentIT;

// ============================================================
// Sidebar.java
// Purpose: Creates the application's navigation sidebar
// ============================================================

// -------------------------
// Import Statements
// -------------------------

import java.awt.*;

import javax.swing.*;

// -------------------------
// Sidebar Class
// -------------------------

public class Sidebar extends JPanel {

	// -------------------------
	// Sidebar Colors
	// -------------------------

	/**
	 * 
	 */
	private static final long serialVersionUID = -624190851312895425L;

	private final Color SIDEBAR_COLOR = new Color(9, 35, 65);

	private final Color BUTTON_COLOR = new Color(9, 35, 65);

	// -------------------------
	// GUI Variables
	// -------------------------

	private CardLayout cardLayout;

	private JPanel contentPanel;

	// -------------------------
	// Sidebar Constructor
	// -------------------------

	public Sidebar(CardLayout cardLayout, JPanel contentPanel) {

		// -------------------------
		// Store References
		// -------------------------

		this.cardLayout = cardLayout;

		this.contentPanel = contentPanel;

		// -------------------------
		// Sidebar Size
		// -------------------------

		setPreferredSize(new Dimension(270, 0));

		// -------------------------
		// Sidebar Color
		// -------------------------

		setBackground(SIDEBAR_COLOR);

		// -------------------------
		// Sidebar Layout
		// -------------------------

		setLayout(new BorderLayout());

		// -------------------------
		// Create Sidebar Sections
		// -------------------------

		add(createHeader(), BorderLayout.NORTH);

		add(createNavigation(), BorderLayout.CENTER);

		// Settings page not intended to be implemented yet, so don't display button
		//add(createSettingsButton(), BorderLayout.SOUTH);
	}

	// =========================================================
	// Header
	// =========================================================

	private JPanel createHeader() {

		// -------------------------
		// Create Header Panel
		// -------------------------

		JPanel header = new JPanel();

		// -------------------------
		// Header Appearance
		// -------------------------

		header.setBackground(SIDEBAR_COLOR);

		// -------------------------
		// Header Layout
		// -------------------------

		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

		// -------------------------
		// Header Spacing
		// -------------------------

		header.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));

		// -------------------------
		// Application Title
		// -------------------------

		JLabel title = new JLabel("Trident Management");

		title.setForeground(Color.WHITE);

		title.setFont(new Font("SansSerif", Font.BOLD, 20));

		// -------------------------
		// Application Subtitle
		// -------------------------

		JLabel subtitle = new JLabel("Equipment Management");

		subtitle.setForeground(new Color(180, 195, 215));

		subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

		// -------------------------
		// Add Text to Header
		// -------------------------

		header.add(title);

		header.add(Box.createVerticalStrut(5));

		header.add(subtitle);

		return header;
	}

	// =========================================================
	// Navigation
	// =========================================================

	private JPanel createNavigation() {

		// -------------------------
		// Create Navigation Panel
		// -------------------------

		JPanel navigation = new JPanel();

		// -------------------------
		// Navigation Appearance
		// -------------------------

		navigation.setBackground(SIDEBAR_COLOR);

		// -------------------------
		// Navigation Layout
		// -------------------------

		navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));

		// -------------------------
		// Navigation Spacing
		// -------------------------

		navigation.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		// -------------------------
		// Dashboard Button
		// -------------------------

		navigation.add(createNavigationButton("Dashboard", "dashboard"));

		// -------------------------
		// Employees Button
		// -------------------------

		navigation.add(createNavigationButton("Employees", "employees"));

		// -------------------------
		// Active Equipment Button
		// -------------------------

		navigation.add(createNavigationButton("Active Equipment", "active"));

		// -------------------------
		// Available Equipment Button
		// -------------------------

		navigation.add(createNavigationButton("Available Equipment", "available"));

		// -------------------------
		// Scan Equipment Button
		// -------------------------

		navigation.add(createNavigationButton("Scan Equipment", "scan"));

		return navigation;
	}

	// =========================================================
	// Navigation Button
	// =========================================================

	private JButton createNavigationButton(String text, String page) {

		// -------------------------
		// Create Button
		// -------------------------

		JButton button = new JButton(text);

		// -------------------------
		// Button Size
		// -------------------------

		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

		button.setPreferredSize(new Dimension(240, 55));

		// -------------------------
		// Button Alignment
		// -------------------------

		button.setHorizontalAlignment(JButton.LEFT);

		// -------------------------
		// Button Font
		// -------------------------

		button.setFont(new Font("SansSerif", Font.PLAIN, 15));

		// -------------------------
		// Button Colors
		// -------------------------

		button.setForeground(Color.WHITE);

		button.setBackground(BUTTON_COLOR);

		// -------------------------
		// Button Border
		// -------------------------

		button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

		// -------------------------
		// Button Appearance
		// -------------------------

		button.setFocusPainted(false);

		button.setOpaque(true);

		// -------------------------
		// Button Action
		// -------------------------

		button.addActionListener(_ -> {

			cardLayout.show(contentPanel, page);
			
			if(page.equals("available")) {
				((AvailableEquipmentPanel) contentPanel.getComponent(3)).refreshTable();
			}

		});

		return button;
	}

	// =========================================================
	// Settings Button
	// =========================================================

	@SuppressWarnings("unused")
	private JPanel createSettingsButton() {

		// -------------------------
		// Create Panel
		// -------------------------

		JPanel panel = new JPanel(new BorderLayout());

		// -------------------------
		// Panel Appearance
		// -------------------------

		panel.setBackground(SIDEBAR_COLOR);

		// -------------------------
		// Panel Spacing
		// -------------------------

		panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15));

		// -------------------------
		// Create Settings Button
		// -------------------------

		JButton settings = createNavigationButton("Settings", "settings");

		// -------------------------
		// Add Settings Button
		// -------------------------

		panel.add(settings, BorderLayout.CENTER);

		return panel;
	}
}