package tridentIT;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3629495377867680010L;

	private static final Color BG_MAIN = new Color(245, 247, 250);
	private static final Color CARD_BG = Color.WHITE;
	private static final Color BORDER_LIGHT = new Color(220, 225, 230);
	private static final Color PRIMARY_BLUE = new Color(35, 90, 160);
	private static final Color TEXT_DARK = new Color(30, 38, 48);
	private static final Color TEXT_MUTED = new Color(100, 110, 120);

	private static final Color BADGE_HIGH_BG = new Color(185, 28, 28);
	private static final Color BADGE_MED_BG = new Color(180, 83, 9);
	private static final Color BADGE_LOW_BG = new Color(21, 128, 61);
	private static final Color BADGE_MAINT_BG = new Color(88, 28, 135);

	private final Consumer<String> navigateCallback;
	private DefaultTableModel watchlistModel;
	private JTable watchlistTable;
	private RiskDonutPanel riskChart;

	private int lowRiskCount = 42;
	private int medRiskCount = 8;
	private int highRiskCount = 5;

	private final Map<String, Integer> accessoryStock = new LinkedHashMap<>();

	private DatabaseManager databaseManager;

	public DashboardPanel(DatabaseManager databaseManager) {
		this.navigateCallback = null;
		this.databaseManager = databaseManager;

		accessoryStock.put("Dell Wireless Mouse", 30);
		accessoryStock.put("USB-C Docking Station", 14);
		accessoryStock.put("HDMI / DisplayPort Cables", 45);
		accessoryStock.put("65W USB-C Laptop Chargers", 19);

		setLayout(new BorderLayout(0, 18));
		setBackground(BG_MAIN);
		setBorder(BorderFactory.createEmptyBorder(26, 30, 26, 30));

		add(buildHeaderSection(), BorderLayout.NORTH);
		add(buildMainContent(), BorderLayout.CENTER);
	}

	private JPanel buildHeaderSection() {
		JPanel container = new JPanel(new BorderLayout(0, 14));
		container.setOpaque(false);

		JPanel topRow = new JPanel(new BorderLayout());
		topRow.setOpaque(false);

		JPanel titleBox = new JPanel();
		titleBox.setOpaque(false);
		titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("IT Asset Operations Dashboard");
		title.setFont(new Font("SansSerif", Font.BOLD, 28));
		title.setForeground(TEXT_DARK);

		JLabel subtitle = new JLabel(
				"Real-time checkout velocity, overdue risk watchlist, repair queue, and bulk consumables.");
		subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
		subtitle.setForeground(TEXT_MUTED);

		titleBox.add(title);
		titleBox.add(Box.createVerticalStrut(4));
		titleBox.add(subtitle);
		topRow.add(titleBox, BorderLayout.WEST);

		JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		quickActions.setOpaque(false);

		JButton bulkCheckoutBtn = createButton("+ Bulk Checkout Cart", true);
		JButton scanReturnBtn = createButton("Scan / Return / Damage", false);
		JButton exportCsvBtn = createButton("Export CSV", false);

		bulkCheckoutBtn.addActionListener(_ -> openBulkCheckoutDialog());
		scanReturnBtn.addActionListener(_ -> triggerNav("SCAN", "Opening Scan & Return Station..."));
		exportCsvBtn.addActionListener(_ -> exportWatchlistToCsv());

		quickActions.add(bulkCheckoutBtn);
		quickActions.add(scanReturnBtn);
		quickActions.add(exportCsvBtn);
		topRow.add(quickActions, BorderLayout.EAST);

		JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 12, 0));
		kpiGrid.setOpaque(false);

		int activeSize = databaseManager.getAllActive().size();
		int empSize = databaseManager.getAllEmp().size();
		int availableSize = databaseManager.getAllAvailable().size();
		int maintSize = databaseManager.getAllActiveMaint().size();

		kpiGrid.add(createKpiCard("Active Loans", activeSize + "", "Across " + empSize + " employees", PRIMARY_BLUE,
				() -> triggerNav("ACTIVE", null)));
		kpiGrid.add(createKpiCard("Available to Assign", availableSize + "", "Ready on shelf", BADGE_LOW_BG,
				() -> triggerNav("AVAILABLE", null)));
		kpiGrid.add(createKpiCard("Overdue Returns", "5", "12 pending due soon", BADGE_HIGH_BG,
				() -> sendOverdueReminders()));
		kpiGrid.add(createKpiCard("In Maintenance / RMA", maintSize + "", "2 awaiting vendor parts", BADGE_MAINT_BG,
				() -> openMaintenanceDrawer()));

		container.add(topRow, BorderLayout.NORTH);
		container.add(kpiGrid, BorderLayout.SOUTH);

		return container;
	}

	private JPanel createKpiCard(String label, String value, String subtext, Color accent, Runnable onClick) {
		JPanel card = new JPanel(new BorderLayout(0, 6));
		card.setBackground(CARD_BG);
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, BORDER_LIGHT),
						BorderFactory.createEmptyBorder(14, 16, 14, 16))));

		JLabel titleLbl = new JLabel(label);
		titleLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
		titleLbl.setForeground(TEXT_MUTED);

		JLabel valLbl = new JLabel(value);
		valLbl.setFont(new Font("SansSerif", Font.BOLD, 26));
		valLbl.setForeground(TEXT_DARK);

		JLabel subLbl = new JLabel(subtext);
		subLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		subLbl.setForeground(accent);

		card.add(titleLbl, BorderLayout.NORTH);
		card.add(valLbl, BorderLayout.CENTER);
		card.add(subLbl, BorderLayout.SOUTH);

		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (onClick != null)
					onClick.run();
			}
		});

		return card;
	}

	private JPanel buildMainContent() {
		JPanel split = new JPanel(new BorderLayout(16, 0));
		split.setOpaque(false);
		split.add(buildActionableWatchlistPanel(), BorderLayout.CENTER);
		split.add(buildRightAnalyticsAndStockColumn(), BorderLayout.EAST);
		return split;
	}

	private JPanel buildActionableWatchlistPanel() {
		JPanel card = new JPanel(new BorderLayout(0, 12));
		card.setBackground(CARD_BG);
		card.setBorder(createCardBorder());

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);

		JLabel title = new JLabel("Action Required: Overdue, High-Risk & Maintenance Queue");
		title.setFont(new Font("SansSerif", Font.BOLD, 16));
		title.setForeground(TEXT_DARK);

		JLabel hint = new JLabel("Select rows for instant reminders, risk overrides, or damage logging");
		hint.setFont(new Font("SansSerif", Font.PLAIN, 12));
		hint.setForeground(TEXT_MUTED);

		JPanel leftHdr = new JPanel();
		leftHdr.setOpaque(false);
		leftHdr.setLayout(new BoxLayout(leftHdr, BoxLayout.Y_AXIS));
		leftHdr.add(title);
		leftHdr.add(hint);

		header.add(leftHdr, BorderLayout.WEST);

		String[] cols = { "Select", "Asset ID", "Equipment", "Holder / Tech", "Dept", "Due / RMA", "Risk", "Status" };
		watchlistModel = new DefaultTableModel(cols, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnIndex == 0 ? Boolean.class : String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}
		};

		watchlistModel.addRow(new Object[] { false, "EQP-00104", "MacBook Pro 16\"", "Sarah Jenkins", "Marketing",
				"Overdue (4d)", "High", "Overdue" });
		watchlistModel.addRow(new Object[] { false, "EQP-00119", "Dell Latitude 7430", "Marcus Vance", "Operations",
				"Overdue (2d)", "High", "Overdue" });
		watchlistModel.addRow(new Object[] { false, "EQP-00124", "ThinkPad X1 Carbon", "RMA #88412 (Screen)",
				"IT Bench", "Est. Nov 18", "Medium", "Maintenance" });

		watchlistTable = new JTable(watchlistModel);
		watchlistTable.setRowHeight(48);
		watchlistTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
		watchlistTable.setShowGrid(false);
		watchlistTable.setSelectionBackground(new Color(220, 235, 255));
		watchlistTable.setSelectionForeground(Color.BLACK);
		watchlistTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
		watchlistTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		watchlistTable.getColumnModel().getColumn(0).setMaxWidth(50);

		watchlistTable.getColumnModel().getColumn(6).setCellRenderer(new HighContrastBadgeRenderer());
		watchlistTable.getColumnModel().getColumn(7).setCellRenderer(new HighContrastBadgeRenderer());

		JScrollPane scroll = new JScrollPane(watchlistTable);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT));

		JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		actionBar.setOpaque(false);

		JButton remindBtn = createButton("Send Overdue Email", true);
		JButton overrideRiskBtn = createButton("Override Risk Level", false);
		JButton reportDamageBtn = createButton("Mark Damaged / RMA", false);

		remindBtn.addActionListener(_ -> sendOverdueReminders());
		overrideRiskBtn.addActionListener(_ -> overrideSelectedRisk());
		reportDamageBtn.addActionListener(_ -> markSelectedAsDamaged());

		actionBar.add(remindBtn);
		actionBar.add(overrideRiskBtn);
		actionBar.add(reportDamageBtn);

		card.add(header, BorderLayout.NORTH);
		card.add(scroll, BorderLayout.CENTER);
		card.add(actionBar, BorderLayout.SOUTH);

		return card;
	}

	private JPanel buildRightAnalyticsAndStockColumn() {
		JPanel rightCol = new JPanel();
		rightCol.setOpaque(false);
		rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
		rightCol.setPreferredSize(new Dimension(330, 0));

		JPanel riskCard = new JPanel(new BorderLayout(0, 8));
		riskCard.setBackground(CARD_BG);
		riskCard.setBorder(createCardBorder());

		JLabel riskTitle = new JLabel("Equipment Risk Breakdown");
		riskTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
		riskTitle.setForeground(TEXT_DARK);
		riskCard.add(riskTitle, BorderLayout.NORTH);

		riskChart = new RiskDonutPanel();
		riskChart.setPreferredSize(new Dimension(300, 160));
		riskCard.add(riskChart, BorderLayout.CENTER);

		JPanel stockCard = new JPanel(new BorderLayout(0, 10));
		stockCard.setBackground(CARD_BG);
		stockCard.setBorder(createCardBorder());

		JLabel stockTitle = new JLabel("Bulk Accessories (No Asset Tag)");
		stockTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
		stockTitle.setForeground(TEXT_DARK);

		JPanel stockList = new JPanel();
		stockList.setOpaque(false);
		stockList.setLayout(new BoxLayout(stockList, BoxLayout.Y_AXIS));

		for (Map.Entry<String, Integer> entry : accessoryStock.entrySet()) {
			stockList.add(createStockCounterRow(entry.getKey(), entry.getValue()));
			stockList.add(Box.createVerticalStrut(8));
		}

		stockCard.add(stockTitle, BorderLayout.NORTH);
		stockCard.add(stockList, BorderLayout.CENTER);

		rightCol.add(riskCard);
		rightCol.add(Box.createVerticalStrut(14));
		rightCol.add(stockCard);

		return rightCol;
	}

	private JPanel createStockCounterRow(String itemName, int initialCount) {
		JPanel row = new JPanel(new BorderLayout(8, 0));
		row.setBackground(BG_MAIN);
		row.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

		JLabel nameLbl = new JLabel(itemName);
		nameLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
		nameLbl.setForeground(TEXT_DARK);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		controls.setOpaque(false);

		JLabel countLbl = new JLabel(String.valueOf(initialCount));
		countLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
		countLbl.setForeground(PRIMARY_BLUE);

		JButton minusBtn = new JButton("-");
		JButton plusBtn = new JButton("+");
		styleMiniCounterButton(minusBtn);
		styleMiniCounterButton(plusBtn);

		minusBtn.addActionListener(_ -> {
			int cur = accessoryStock.get(itemName);
			if (cur > 0) {
				accessoryStock.put(itemName, cur - 1);
				countLbl.setText(String.valueOf(cur - 1));
			}
		});

		plusBtn.addActionListener(_ -> {
			int cur = accessoryStock.get(itemName);
			accessoryStock.put(itemName, cur + 1);
			countLbl.setText(String.valueOf(cur + 1));
		});

		controls.add(minusBtn);
		controls.add(countLbl);
		controls.add(plusBtn);

		row.add(nameLbl, BorderLayout.CENTER);
		row.add(controls, BorderLayout.EAST);
		return row;
	}

	private void styleMiniCounterButton(JButton btn) {
		btn.setFont(new Font("SansSerif", Font.BOLD, 12));
		btn.setMargin(new Insets(2, 6, 2, 6));
		btn.setFocusPainted(false);
		btn.setBackground(Color.WHITE);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private void openBulkCheckoutDialog() {
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bulk Checkout Cart", true);
		dialog.setSize(480, 340);
		dialog.setLocationRelativeTo(this);
		dialog.setLayout(new BorderLayout(12, 12));

		JPanel content = new JPanel(new BorderLayout(10, 10));
		content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

		JPanel topFields = new JPanel(new GridLayout(2, 2, 8, 8));
		topFields.add(new JLabel("Employee Name / ID:"));
		JTextField empField = new JTextField("Sarah Jenkins");
		topFields.add(empField);

		topFields.add(new JLabel("Scan Asset ID:"));
		JTextField scanField = new JTextField();
		topFields.add(scanField);

		DefaultListModel<String> cartModel = new DefaultListModel<>();
		cartModel.addElement("EQP-00131 — Dell Latitude 7430");
		JList<String> cartList = new JList<>(cartModel);

		scanField.addActionListener(_ -> {
			String code = scanField.getText().trim();
			if (!code.isEmpty()) {
				cartModel.addElement(code.toUpperCase() + " — Scanned Hardware Item");
				scanField.setText("");
			}
		});

		JButton confirmBtn = createButton("Complete Bulk Assignment", true);
		confirmBtn.addActionListener(_ -> {
			JOptionPane.showMessageDialog(dialog, "Assigned items successfully.");
			dialog.dispose();
		});

		content.add(topFields, BorderLayout.NORTH);
		content.add(new JScrollPane(cartList), BorderLayout.CENTER);
		content.add(confirmBtn, BorderLayout.SOUTH);

		dialog.add(content);
		dialog.setVisible(true);
	}

	private void sendOverdueReminders() {
		JOptionPane.showMessageDialog(this, "Queued automated email return reminders for overdue asset holders.");
	}

	private void overrideSelectedRisk() {
		int row = watchlistTable.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Select a row first.");
			return;
		}
		String[] options = { "Low", "Medium", "High" };
		String choice = (String) JOptionPane.showInputDialog(this, "Set risk level:", "Risk Override",
				JOptionPane.PLAIN_MESSAGE, null, options, "High");
		if (choice != null) {
			watchlistModel.setValueAt(choice, row, 6);
			riskChart.repaint();
		}
	}

	private void markSelectedAsDamaged() {
		int row = watchlistTable.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Select an asset row first.");
			return;
		}
		String id = (String) watchlistModel.getValueAt(row, 1);
		String note = JOptionPane.showInputDialog(this, "Enter damage description for " + id + ":");
		if (note != null && !note.trim().isEmpty()) {
			watchlistModel.setValueAt("RMA: " + note.trim(), row, 5);
			watchlistModel.setValueAt("Maintenance", row, 7);
		}
	}

	private void openMaintenanceDrawer() {
		JOptionPane.showMessageDialog(this, "Active Maintenance Queue: 2 devices awaiting parts.");
	}

	private void exportWatchlistToCsv() {
		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(new File("it_dashboard_watchlist.csv"));
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try (PrintWriter out = new PrintWriter(chooser.getSelectedFile())) {
				for (int c = 1; c < watchlistTable.getColumnCount(); c++) {
					out.print(watchlistTable.getColumnName(c) + (c < watchlistTable.getColumnCount() - 1 ? "," : ""));
				}
				out.println();
				for (int r = 0; r < watchlistTable.getRowCount(); r++) {
					for (int c = 1; c < watchlistTable.getColumnCount(); c++) {
						String val = String.valueOf(watchlistTable.getValueAt(r, c)).replace("\"", "\"\"");
						out.print("\"" + val + "\"" + (c < watchlistTable.getColumnCount() - 1 ? "," : ""));
					}
					out.println();
				}
				JOptionPane.showMessageDialog(this, "CSV exported successfully.");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
			}
		}
	}

	private void triggerNav(String route, String fallbackMsg) {
		if (navigateCallback != null) {
			navigateCallback.accept(route);
		} else if (fallbackMsg != null) {
			JOptionPane.showMessageDialog(this, fallbackMsg);
		}
	}

	private Border createCardBorder() {
		return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_LIGHT),
				BorderFactory.createEmptyBorder(16, 18, 16, 18));
	}

	private JButton createButton(String text, boolean primary) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("SansSerif", Font.BOLD, 12));
		btn.setFocusPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		if (primary) {
			btn.setBackground(PRIMARY_BLUE);
			btn.setForeground(Color.WHITE);
			btn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
		} else {
			btn.setBackground(Color.WHITE);
			btn.setForeground(new Color(50, 60, 70));
			btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)),
					BorderFactory.createEmptyBorder(8, 13, 8, 13)));
		}
		return btn;
	}

	private static class HighContrastBadgeRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8005301962423612949L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JPanel wrapper = new JPanel(new GridBagLayout());
			wrapper.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
			String text = value == null ? "" : value.toString();
			JLabel badge = new JLabel(text);
			badge.setFont(new Font("SansSerif", Font.BOLD, 11));
			badge.setForeground(Color.WHITE);
			badge.setOpaque(true);
			badge.setBorder(BorderFactory.createEmptyBorder(4, 9, 4, 9));

			switch (text.toLowerCase()) {
			case "high":
			case "overdue":
				badge.setBackground(BADGE_HIGH_BG);
				break;
			case "medium":
				badge.setBackground(BADGE_MED_BG);
				break;
			case "maintenance":
				badge.setBackground(BADGE_MAINT_BG);
				break;
			default:
				badge.setBackground(BADGE_LOW_BG);
				break;
			}
			wrapper.add(badge);
			return wrapper;
		}
	}

	private class RiskDonutPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6979930619710735582L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int total = lowRiskCount + medRiskCount + highRiskCount;
			int size = 120;
			int x = 14;
			int y = (getHeight() - size) / 2;

			int lowAngle = (int) Math.round(360.0 * lowRiskCount / total);
			int medAngle = (int) Math.round(360.0 * medRiskCount / total);
			int highAngle = 360 - lowAngle - medAngle;

			int start = 90;
			g2.setColor(BADGE_LOW_BG);
			g2.fillArc(x, y, size, size, start, lowAngle);
			start += lowAngle;
			g2.setColor(BADGE_MED_BG);
			g2.fillArc(x, y, size, size, start, medAngle);
			start += medAngle;
			g2.setColor(BADGE_HIGH_BG);
			g2.fillArc(x, y, size, size, start, highAngle);

			g2.setColor(CARD_BG);
			g2.fillOval(x + 30, y + 30, 60, 60);

			g2.setColor(TEXT_DARK);
			g2.setFont(new Font("SansSerif", Font.BOLD, 16));
			FontMetrics fm = g2.getFontMetrics();
			String totalStr = String.valueOf(total);
			g2.drawString(totalStr, x + 60 - fm.stringWidth(totalStr) / 2, y + 65);

			int lx = x + size + 16;
			int ly = y + 25;
			drawLegendItem(g2, lx, ly, BADGE_LOW_BG, "Low: " + lowRiskCount);
			drawLegendItem(g2, lx, ly + 28, BADGE_MED_BG, "Med: " + medRiskCount);
			drawLegendItem(g2, lx, ly + 56, BADGE_HIGH_BG, "High: " + highRiskCount);
			g2.dispose();
		}

		private void drawLegendItem(Graphics2D g2, int x, int y, Color c, String label) {
			g2.setColor(c);
			g2.fillRoundRect(x, y - 8, 10, 10, 3, 3);
			g2.setColor(TEXT_DARK);
			g2.setFont(new Font("SansSerif", Font.BOLD, 11));
			g2.drawString(label, x + 15, y);
		}
	}
}