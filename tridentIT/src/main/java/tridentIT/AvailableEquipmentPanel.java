package tridentIT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class AvailableEquipmentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3211932795523923824L;
	
	private DefaultTableModel tableModel;

	public AvailableEquipmentPanel(DatabaseManager databaseManager) {
		setBackground(new Color(245, 247, 250));
		setLayout(new BorderLayout(0, 20));
		setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);

		JLabel title = new JLabel("Available Equipment");
		title.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel description = new JLabel("Equipment that is currently available to be assigned.");
		description.setForeground(new Color(100, 110, 120));

		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.add(title);
		titlePanel.add(Box.createVerticalStrut(5));
		titlePanel.add(description);
		header.add(titlePanel, BorderLayout.WEST);

		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.setOpaque(false);
		JTextField searchField = new JTextField();
		searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));
		searchField
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)),
						BorderFactory.createEmptyBorder(10, 12, 10, 12)));
		searchPanel.add(searchField, BorderLayout.CENTER);

		String[] columns = { "Asset ID", "Equipment", "Type", "Condition", "Date Added" };
		List<List<Object>> available = databaseManager.getAllAvailable();

		tableModel = new DefaultTableModel();
		
		JTable table = new JTable(tableModel) {
			public boolean isCellEditable(int nRow, int nCol) {
				return false;
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableModel.setColumnIdentifiers(columns);
		table.setRowHeight(55);
		table.setFont(new Font("SansSerif", Font.PLAIN, 14));
		table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
		table.getTableHeader().setPreferredSize(new Dimension(0, 45));
		table.setShowGrid(false);
		table.setSelectionBackground(new Color(220, 235, 255));
		
		for(List<Object> row : available)
		{
			tableModel.addRow(row.toArray());
		}

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

		JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
		centerPanel.setOpaque(false);
		centerPanel.add(searchPanel, BorderLayout.NORTH);
		centerPanel.add(scrollPane, BorderLayout.CENTER);

		add(header, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
	}
}