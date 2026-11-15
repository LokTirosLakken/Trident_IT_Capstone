package tridentIT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.*;

public class ScanEquipmentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5318246191197704328L;

	public ScanEquipmentPanel() {
		setBackground(new Color(245, 247, 250));
		setLayout(new BorderLayout(0, 20));
		setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

		JPanel header = new JPanel();
		header.setOpaque(false);
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("Scan Equipment");
		title.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel description = new JLabel(
				"Scan an equipment QR code or barcode to inspect specs, check out, or report damage.");
		description.setForeground(new Color(100, 110, 120));

		header.add(title);
		header.add(Box.createVerticalStrut(5));
		header.add(description);

		JPanel centerCard = new JPanel(new BorderLayout(15, 15));
		centerCard.setBackground(Color.WHITE);
		centerCard
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)),
						BorderFactory.createEmptyBorder(24, 24, 24, 24)));

		JPanel scanInputPanel = new JPanel(new BorderLayout(10, 0));
		scanInputPanel.setOpaque(false);
		JTextField scanField = new JTextField();
		scanField.setFont(new Font("SansSerif", Font.PLAIN, 16));
		scanField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)),
				BorderFactory.createEmptyBorder(10, 12, 10, 12)));
		JButton scanBtn = new JButton("Lookup Asset");
		scanBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
		scanBtn.setBackground(new Color(35, 90, 160));
		scanBtn.setForeground(Color.WHITE);
		scanBtn.setFocusPainted(false);

		scanInputPanel.add(new JLabel("Asset Barcode / ID: "), BorderLayout.WEST);
		scanInputPanel.add(scanField, BorderLayout.CENTER);
		scanInputPanel.add(scanBtn, BorderLayout.EAST);

		JTextArea resultArea = new JTextArea(
				"Scan or enter an asset ID above to view hardware specs, current custody, and action buttons (Take Back, Mark Damaged, Print Label)...\n");
		resultArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
		resultArea.setEditable(false);
		resultArea.setBackground(new Color(245, 247, 250));
		resultArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		scanField.addActionListener(_ -> {
			String code = scanField.getText().trim();
			if (!code.isEmpty()) {
				resultArea.setText("Loaded Asset Details: [" + code.toUpperCase()
						+ "]\n• Type: Dell Latitude 7430\n• Status: Assigned\n• Holder: Sarah Jenkins (Marketing)\n• Condition: Good");
				scanField.setText("");
			}
		});

		scanBtn.addActionListener(_ -> {
			String code = scanField.getText().trim();
			if (!code.isEmpty()) {
				resultArea.setText("Loaded Asset Details: [" + code.toUpperCase()
						+ "]\n• Type: Dell Latitude 7430\n• Status: Assigned\n• Holder: Sarah Jenkins (Marketing)\n• Condition: Good");
				scanField.setText("");
			}
		});

		centerCard.add(scanInputPanel, BorderLayout.NORTH);
		centerCard.add(new JScrollPane(resultArea), BorderLayout.CENTER);

		add(header, BorderLayout.NORTH);
		add(centerCard, BorderLayout.CENTER);
	}
}