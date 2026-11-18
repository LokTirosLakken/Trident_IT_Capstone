package tridentIT;

// ============================================================
// EmployeesPanel.java
// Purpose: Displays the Employees screen
// ============================================================

// -------------------------
// Import Statements
// -------------------------

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

// -------------------------
// Employees Panel Class
// -------------------------

public class EmployeesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// -------------------------
	// Colors (change these to restyle the page)
	// -------------------------

	// Darker outline used by the search box, table card and tip box (change this to
	// adjust all three)
	private static final Color CARD_BORDER = new Color(140, 155, 175);
	private static final Color TEXT_DARK = new Color(30, 41, 59);
	private static final Color TEXT_GRAY = new Color(100, 116, 139);
	private static final Color BLUE = new Color(37, 99, 235);

	// Color for everything in the tip box (same as the border color)
	private static final Color TIP_COLOR = CARD_BORDER;

	// -------------------------
	// Table Variables
	// -------------------------

	private final DefaultTableModel model;
	private final JTable table;
	private final TableRowSorter<DefaultTableModel> sorter;

	// used to load and save employees
	private final DatabaseManager databaseManager;

	// -------------------------
	// Constructor
	// -------------------------

	public EmployeesPanel(DatabaseManager databaseManager) {

		// remember the database so we can load and save employees
		this.databaseManager = databaseManager;

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

		JLabel title = new JLabel("Employees");

		// -------------------------
		// Title Font
		// -------------------------

		title.setFont(new Font("SansSerif", Font.BOLD, 28));

		// -------------------------
		// Add Title
		// -------------------------

		add(title, BorderLayout.NORTH);

		// ====================================================
		// everything below was added to the original page
		// ====================================================

		// -------------------------
		// Table Columns
		// -------------------------

		String[] columns = { "Employee ID", "Name", "Department", "Email", "Active Equipment", "" };

		// Table model (cells can't be edited by typing)
		model = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(model);
		sorter = new TableRowSorter<>(model);
		table.setRowSorter(sorter);

		styleTable();

		// fill the table with employees from the database
		loadEmployees();

		// -------------------------
		// Add Subtitle, Search Bar, Table, and Tip
		// -------------------------

		JPanel content = new JPanel(new BorderLayout());
		content.setOpaque(false);
		content.add(buildTop(), BorderLayout.NORTH);
		content.add(buildBody(), BorderLayout.CENTER);

		add(content, BorderLayout.CENTER);
	}

	// -------------------------
	// Top Section: subtitle, search bar, Add button
	// -------------------------

	private JPanel buildTop() {

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);

		// Subtitle (sits right under the original title)
		JLabel subtitle = new JLabel("Browse and manage company employees.");
		subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
		subtitle.setForeground(TEXT_GRAY);

		// Search box
		SearchField search = new SearchField("Search employees...");
		search.setPreferredSize(new Dimension(300, 42));

		// Filter the table every time the text changes
		search.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				applyFilter(search.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				applyFilter(search.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				applyFilter(search.getText());
			}
		});

		// Add Employee button
		BlueButton addButton = new BlueButton("+   Add Employee");
		addButton.setPreferredSize(new Dimension(160, 42));
		addButton.addActionListener(_ -> openAddEmployeeDialog());

		JPanel searchRow = new JPanel(new BorderLayout(15, 0));
		searchRow.setOpaque(false);
		searchRow.setBorder(BorderFactory.createEmptyBorder(22, 0, 20, 0));
		searchRow.add(search, BorderLayout.CENTER);
		searchRow.add(addButton, BorderLayout.EAST);

		top.add(subtitle, BorderLayout.NORTH);
		top.add(searchRow, BorderLayout.CENTER);
		return top;
	}

	// -------------------------
	// Body Section: table card and tip box
	// -------------------------

	private JPanel buildBody() {

		JPanel body = new JPanel(new BorderLayout(0, 20));
		body.setOpaque(false);

		// Table card (white rounded box)
		RoundedPanel tableCard = new RoundedPanel(Color.WHITE, CARD_BORDER);
		tableCard.setLayout(new BorderLayout());
		tableCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
		// The table sits in a scroll pane, so a long list scrolls instead of running
		// off the page
		// (the scroll pane shows the table header at the top by itself)
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.getVerticalScrollBar().setUnitIncrement(24);

		// Keep the top-right corner white when the scroll bar appears
		JPanel corner = new JPanel();
		corner.setBackground(Color.WHITE);
		scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);

		tableCard.add(scrollPane, BorderLayout.CENTER);

		// Tip box (light blue)
		RoundedPanel tip = new RoundedPanel(Color.WHITE, TIP_COLOR);
		tip.setLayout(new BorderLayout(12, 0));
		tip.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

		JLabel tipTitle = new JLabel("Tip");
		tipTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
		tipTitle.setForeground(TIP_COLOR);

		JLabel tipText = new JLabel("Click on an employee to view detailed information and assigned equipment.");
		tipText.setFont(new Font("SansSerif", Font.PLAIN, 12));
		tipText.setForeground(TIP_COLOR);

		// "Tip" with the sentence to its right, on one line
		JPanel tipLines = new JPanel();
		tipLines.setOpaque(false);
		tipLines.setLayout(new BoxLayout(tipLines, BoxLayout.X_AXIS));
		tipLines.add(tipTitle);
		tipLines.add(Box.createHorizontalStrut(14));
		tipLines.add(tipText);

		// Icon centered up and down next to the text
		JPanel iconHolder = new JPanel(new GridBagLayout());
		iconHolder.setOpaque(false);
		iconHolder.add(new InfoIcon());

		tip.add(iconHolder, BorderLayout.WEST);
		tip.add(tipLines, BorderLayout.CENTER);

		// Keep the tip box from stretching tall
		JPanel tipHolder = new JPanel(new BorderLayout());
		tipHolder.setOpaque(false);
		tipHolder.add(tip, BorderLayout.NORTH);

		// The table card fills the space; the tip box stays pinned at the bottom of the
		// page
		body.add(tableCard, BorderLayout.CENTER);
		body.add(tipHolder, BorderLayout.SOUTH);
		return body;
	}

	// -------------------------
	// Table Styling
	// -------------------------

	private void styleTable() {

		table.setRowHeight(54);
		table.setFont(new Font("SansSerif", Font.PLAIN, 13));
		table.setForeground(TEXT_DARK);
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(true);
		table.setGridColor(new Color(237, 241, 246));
		table.setIntercellSpacing(new Dimension(0, 1));
		table.setRowSelectionAllowed(false);
		table.setFocusable(false);
		table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Column widths
		int[] widths = { 110, 150, 150, 260, 150, 40 };
		for (int i = 0; i < widths.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
		table.getColumnModel().getColumn(5).setMaxWidth(50);

		// Plain text columns get some left padding
		DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer();
		textRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
		for (int i = 0; i < 4; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
		}

		// Pill column and arrow column
		table.getColumnModel().getColumn(4).setCellRenderer(new PillRenderer());

		table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row,
					int col) {
				JLabel arrow = new JLabel("\u203A", SwingConstants.CENTER);
				arrow.setFont(new Font("SansSerif", Font.PLAIN, 26));
				arrow.setForeground(TEXT_GRAY);
				arrow.setOpaque(true);
				arrow.setBackground(Color.WHITE);
				return arrow;
			}
		});

		// Header styling
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row,
					int col) {
				super.getTableCellRendererComponent(t, value, sel, focus, row, col);
				setFont(new Font("SansSerif", Font.BOLD, 12));
				setForeground(TEXT_GRAY);
				setBackground(Color.WHITE);
				setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(237, 241, 246)),
						BorderFactory.createEmptyBorder(0, 12, 0, 12)));
				return this;
			}
		};
		table.getTableHeader().setDefaultRenderer(headerRenderer);
		table.getTableHeader().setPreferredSize(new Dimension(0, 44));
		table.getTableHeader().setReorderingAllowed(false);

		// Click a row to see details (with Edit and Delete buttons)
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int viewRow = table.rowAtPoint(e.getPoint());
				if (viewRow < 0) {
					return;
				}
				showEmployeeOptions(table.convertRowIndexToModel(viewRow));
			}
		});
	}

	// -------------------------
	// Load Employees From the Database
	// -------------------------

	private void loadEmployees() {
		model.setRowCount(0);

		List<List<Object>> available = databaseManager.getAllEmp();
		try {
			for (List<Object> row : available) {
				model.addRow(row.toArray());
			}
		} catch (Exception e) {
			System.out.println("Failed to get all employees.");
		}
	}

	// -------------------------
	// Add Employee Popup
	// -------------------------

	private void openAddEmployeeDialog() {
		AddEmployeeDialog dialog = new AddEmployeeDialog(SwingUtilities.getWindowAncestor(this), databaseManager);
		dialog.setVisible(true); // waits here until the popup closes

		if (dialog.isSaved()) {
			loadEmployees(); // show the new employee
		}
	}

	// -------------------------
	// Details popup with Edit / Delete / Close
	// -------------------------

	private void showEmployeeOptions(int r) {

		Object[] employee = { String.valueOf(model.getValueAt(r, 0)), String.valueOf(model.getValueAt(r, 1)),
				String.valueOf(model.getValueAt(r, 2)), String.valueOf(model.getValueAt(r, 3)) };

		String message = "Employee ID: " + ((String) employee[0]) + "\nName: " + ((String) employee[1])
				+ "\nDepartment: " + ((String) employee[2]) + "\nEmail: " + ((String) employee[3])
				+ "\nActive Equipment: " + model.getValueAt(r, 4);

		Object[] options = { "Edit", "Delete", "Close" };

		int choice = JOptionPane.showOptionDialog(this, message, "Employee Details", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);

		if (choice == 0) {
			openEditEmployeeDialog(employee);
		} else if (choice == 1) {
			deleteEmployee(Integer.parseInt((String) employee[0]), message);
		}
	}

	// -------------------------
	// Edit Employee Popup
	// -------------------------

	private void openEditEmployeeDialog(Object[] emp) {
		AddEmployeeDialog dialog = new AddEmployeeDialog(SwingUtilities.getWindowAncestor(this), databaseManager, emp);
		dialog.setVisible(true);

		if (dialog.isSaved()) {
			loadEmployees(); // show the changes
		}
	}

	// -------------------------
	// Delete Employee
	// -------------------------

	private void deleteEmployee(int id, String name) {

		int confirm = JOptionPane.showConfirmDialog(this, "Delete " + name + " (" + id + ")?\nThis cannot be undone.",
				"Delete Employee", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		if (databaseManager.deleteEmployee(id)) {
			loadEmployees(); // remove it from the table
		} else {
			JOptionPane.showMessageDialog(this, "The employee could not be deleted. Check the Console for details.",
					"Delete Employee", JOptionPane.WARNING_MESSAGE);
		}
	}

	// -------------------------
	// Search Filter
	// -------------------------

	private void applyFilter(String text) {
		if (text.trim().isEmpty()) {
			sorter.setRowFilter(null);
		} else {
			sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text.trim())));
		}
	}

	// ============================================================
	// Small helper classes (each one draws one piece of the design)
	// ============================================================

	// White rounded box with a thin border
	private static class RoundedPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private final Color fill;
		private final Color line;

		RoundedPanel(Color fill, Color line) {
			this.fill = fill;
			this.line = line;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(fill);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
			g2.setColor(line);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	// Search box with a magnifying glass and gray hint text
	private static class SearchField extends JTextField {

		private static final long serialVersionUID = 1L;
		private final String hint;

		SearchField(String hint) {
			this.hint = hint;
			setOpaque(false);
			setFont(new Font("SansSerif", Font.PLAIN, 13));
			setForeground(TEXT_DARK);
			setBorder(BorderFactory.createEmptyBorder(0, 46, 0, 12));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Box
			g2.setColor(Color.WHITE);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
			g2.setColor(CARD_BORDER);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

			// Magnifying glass
			int cy = getHeight() / 2;
			g2.setColor(TEXT_GRAY);
			g2.setStroke(new BasicStroke(2f));
			g2.drawOval(15, cy - 10, 15, 15);
			g2.drawLine(27, cy + 2, 34, cy + 9);

			// Hint text when empty
			if (getText().isEmpty()) {
				g2.setFont(getFont());
				FontMetrics fm = g2.getFontMetrics();
				int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
				g2.setColor(new Color(148, 163, 184));
				g2.drawString(hint, 46, y);
			}

			g2.dispose();
			super.paintComponent(g);
		}
	}

	// Blue rounded button
	private static class BlueButton extends JButton {

		private static final long serialVersionUID = 1L;

		BlueButton(String text) {
			super(text);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setForeground(Color.WHITE);
			setFont(new Font("SansSerif", Font.BOLD, 13));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(getModel().isPressed() ? new Color(23, 50, 130) : new Color(29, 64, 175));
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.dispose();
			super.paintComponent(g);
		}
	}

	// Round "i" icon for the tip box
	private static class InfoIcon extends JComponent {

		private static final long serialVersionUID = 1L;

		InfoIcon() {
			setPreferredSize(new Dimension(30, 30));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(TIP_COLOR);
			g2.setStroke(new BasicStroke(1.6f));
			g2.drawOval(1, 1, 26, 26);
			g2.fillOval(12, 7, 4, 4);
			g2.fillRoundRect(12, 13, 4, 9, 2, 2);
			g2.dispose();
		}
	}

	// Blue (or gray) pill for the "Active Equipment" column
	private static class PillRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			String text = String.valueOf(value);
			boolean empty = text.startsWith("0");

			final Color pillBg = empty ? new Color(237, 240, 244) : new Color(219, 234, 254);
			Color pillFg = empty ? TEXT_GRAY : BLUE;

			JLabel pill = new JLabel(text, SwingConstants.CENTER) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(pillBg);
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
					g2.dispose();
					super.paintComponent(g);
				}
			};
			pill.setOpaque(false);
			pill.setForeground(pillFg);
			pill.setFont(new Font("SansSerif", Font.PLAIN, 11));
			pill.setPreferredSize(new Dimension(54, 24));

			JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
			cell.setBackground(Color.WHITE);
			cell.add(pill);
			return cell;
		}
	}
}