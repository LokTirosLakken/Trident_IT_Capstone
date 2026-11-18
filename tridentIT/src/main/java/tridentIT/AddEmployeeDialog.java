package tridentIT;

// ============================================================
// AddEmployeeDialog.java
// Purpose: Popup window used to add a new employee
// ============================================================

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddEmployeeDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// -------------------------
	// Variables
	// -------------------------

	private final DatabaseManager databaseManager;

	// the employee being edited (null when adding a new one)
	private final Object[] existing;

	// the employee ID is typed in (it comes from the organization)
	private final JTextField idField = new JTextField(20);
	// first and last name are separate, and both are required
	private final JTextField firstNameField = new JTextField(20);
	private final JTextField lastNameField = new JTextField(20);
	private final JComboBox<String> departmentBox = new JComboBox<>(
			new String[] { "IT", "HR", "Finance", "Marketing", "Operations" });
	private final JTextField emailField = new JTextField(20);

	// Becomes true only when the employee was saved to the database
	private boolean saved = false;

	// -------------------------
	// Constructor
	// -------------------------

	// Add mode
	public AddEmployeeDialog(Window owner, DatabaseManager databaseManager) {
		this(owner, databaseManager, null);
	}

	// Edit mode (pass the employee to edit), or Add mode (pass null)
	public AddEmployeeDialog(Window owner, DatabaseManager databaseManager, Object[] existing) {

		super(owner, existing == null ? "Add Employee" : "Edit Employee", Dialog.ModalityType.APPLICATION_MODAL);

		this.databaseManager = databaseManager;
		this.existing = existing;

		// Let the user type a department that isn't in the list
		departmentBox.setEditable(true);

		// When editing, start with the employee's current details
		if (existing != null) {
			// The ID can't be changed when editing (equipment will be linked to it)
			idField.setText((String) existing[0]); // Emp_ID
			idField.setEditable(false);
			// The saved name is "First Last": the part before the first space is the first
			// name,
			// everything after it is the last name
			String fullName = ((String) existing[1]).trim(); // Name
			int space = fullName.indexOf(' ');
			firstNameField.setText(space < 0 ? fullName : fullName.substring(0, space));
			lastNameField.setText(space < 0 ? "" : fullName.substring(space + 1).trim());
			departmentBox.setSelectedItem((String) existing[2]); // Department
			emailField.setText((String) existing[3]); // Email
		}

		// Form (labels on the left, fields on the right)
		JPanel form = new JPanel(new GridBagLayout());
		form.setBackground(Color.WHITE);
		form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

		addRow(form, 0, "Employee ID", idField);
		addRow(form, 1, "First Name", firstNameField);
		addRow(form, 2, "Last Name", lastNameField);
		addRow(form, 3, "Department", departmentBox);
		addRow(form, 4, "Email", emailField);

		// Buttons
		JButton saveButton = new JButton("Save");
		JButton cancelButton = new JButton("Cancel");

		saveButton.addActionListener(_ -> save());
		cancelButton.addActionListener(_ -> dispose());

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		buttons.setBackground(Color.WHITE);
		buttons.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
		buttons.add(cancelButton);
		buttons.add(saveButton);

		// Put it all together
		getContentPane().setBackground(Color.WHITE);
		add(form, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);

		// Pressing Enter clicks Save
		getRootPane().setDefaultButton(saveButton);

		pack();
		setResizable(false);
		setLocationRelativeTo(owner);
	}

	// -------------------------
	// Did the user save?
	// -------------------------

	public boolean isSaved() {
		return saved;
	}

	// -------------------------
	// Save Button
	// -------------------------

	private void save() {

		String id = idField.getText().trim();
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String name = firstName + " " + lastName; // saved as one name, like "Sarah Jones"
		Object selected = departmentBox.getSelectedItem();
		String department = selected == null ? "" : selected.toString().trim();
		String email = emailField.getText().trim();

		// Check that everything was filled in
		if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || department.isEmpty() || email.isEmpty()) {
			showWarning("Please fill in every field.");
			return;
		}

		// Very simple email check
		int at = email.indexOf('@');
		if (at < 1 || email.indexOf('.', at) < 0) {
			showWarning("Please enter a valid email address (for example name@company.com).");
			return;
		}

		// A new employee can't reuse an ID that is already taken
		if (existing == null && databaseManager.employeeExists(id)) {
			showWarning("That employee ID is already in use. Please enter a different one.");
			return;
		}

		// Save to the database (update if editing, add if new)
		boolean ok;
		if (existing == null) {
			ok = databaseManager.addEmp(Integer.parseInt(id), name, department, email);
		} else {
			ok = databaseManager.updateEmployee(Integer.parseInt(id), name, department, email);
		}

		if (ok) {
			saved = true;
			dispose(); // close the popup
		} else {
			showWarning("The employee could not be saved. Check the Console for details.");
		}
	}

	private void showWarning(String message) {
		JOptionPane.showMessageDialog(this, message, "Add Employee", JOptionPane.WARNING_MESSAGE);
	}

	// -------------------------
	// Helper: one label + field row
	// -------------------------

	private void addRow(JPanel form, int row, String labelText, JComponent field) {

		JLabel label = new JLabel(labelText);
		label.setFont(new Font("SansSerif", Font.BOLD, 12));
		label.setForeground(new Color(30, 41, 59));

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = row;
		c.insets = new Insets(6, 0, 6, 12);
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 0;
		form.add(label, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.insets = new Insets(6, 0, 6, 0);
		form.add(field, c);
	}
}