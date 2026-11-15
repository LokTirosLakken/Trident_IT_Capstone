package tridentIT;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class EmployeesPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6111411798426401962L;
	private static final Color BG_MAIN = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_LIGHT = new Color(220, 225, 230);
    private static final Color PRIMARY_BLUE = new Color(35, 90, 160);
    private static final Color TEXT_DARK = new Color(30, 38, 48);
    private static final Color TEXT_MUTED = new Color(100, 110, 120);

    private DefaultTableModel employeeModel;
    private JTable employeeTable;

    public EmployeesPanel() {
        this(null);
    }

    public EmployeesPanel(Runnable backToDashboardCallback) {
        setLayout(new BorderLayout(0, 18));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(26, 30, 26, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Employee Directory & Equipment Custody");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);

        JLabel subtitle = new JLabel("Manage employee records, department assignments, and active checked-out items.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subtitle);
        header.add(titleBox, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        if (backToDashboardCallback != null) {
            JButton backBtn = createButton("Back to Dashboard", false);
			backBtn.addActionListener(_ -> backToDashboardCallback.run());
            actions.add(backBtn);
        }

        JButton addEmployeeBtn = createButton("+ Add Employee", true);
        addEmployeeBtn.addActionListener(_ -> openAddEmployeeDialog());
        actions.add(addEmployeeBtn);

        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel centerCard = new JPanel(new BorderLayout(0, 12));
        centerCard.setBackground(CARD_BG);
        centerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchBarPanel.add(new JLabel("Search Employees: "), BorderLayout.WEST);
        searchBarPanel.add(searchField, BorderLayout.CENTER);

        String[] columns = {
            "Employee ID",
            "Name",
            "Department",
            "Email",
            "Items Checked Out"
        };

        employeeModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeModel.addRow(new Object[]{"EMP-1001", "Sarah Jenkins", "Marketing", "s.jenkins@company.com", "EQP-00104 (MacBook Pro 16\"), EQP-00150 (Wireless Mouse)"});
        employeeModel.addRow(new Object[]{"EMP-1002", "Marcus Vance", "Operations", "m.vance@company.com", "EQP-00119 (Dell Latitude 7430)"});
        employeeModel.addRow(new Object[]{"EMP-1003", "Elena Rostova", "Finance", "e.rostova@company.com", "EQP-00138 (HP Monitor 27\" )"});
        employeeModel.addRow(new Object[]{"EMP-1004", "David Kim", "Engineering", "d.kim@company.com", "EQP-00151 (ThinkPad X1), EQP-00172 (USB-C Dock)"});

        employeeTable = new JTable(employeeModel);
        employeeTable.setRowHeight(50);
        employeeTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        employeeTable.setShowGrid(false);
        employeeTable.setSelectionBackground(new Color(220, 235, 255));
        employeeTable.setSelectionForeground(Color.BLACK);
        employeeTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        employeeTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT));

        centerCard.add(searchBarPanel, BorderLayout.NORTH);
        centerCard.add(scrollPane, BorderLayout.CENTER);
        add(centerCard, BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(employeeModel);
        employeeTable.setRowSorter(sorter);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
    }

    private void openAddEmployeeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Employee", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 12));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField idField = new JTextField("EMP-10" + (employeeModel.getRowCount() + 1));
        JTextField nameField = new JTextField();
        JComboBox<String> deptBox = new JComboBox<>(new String[]{"IT", "Marketing", "Operations", "Finance", "HR", "Engineering"});
        JTextField emailField = new JTextField();

        form.add(new JLabel("Employee ID:")); form.add(idField);
        form.add(new JLabel("Full Name:")); form.add(nameField);
        form.add(new JLabel("Department:")); form.add(deptBox);
        form.add(new JLabel("Email Address:")); form.add(emailField);

        JButton saveBtn = createButton("Save Employee", true);
        saveBtn.addActionListener(_ -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an employee name.");
                return;
            }
            employeeModel.addRow(new Object[]{
                idField.getText().trim(),
                nameField.getText().trim(),
                deptBox.getSelectedItem().toString(),
                emailField.getText().trim().isEmpty() ? nameField.getText().toLowerCase().replace(" ", ".") + "@company.com" : emailField.getText().trim(),
                "None"
            });
            dialog.dispose();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
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
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 215, 220)),
                    BorderFactory.createEmptyBorder(8, 13, 8, 13)
            ));
        }
        return btn;
    }
}