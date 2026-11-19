package tridentIT;

// ======================================================================
// DateRenderer.java
// Purpose: Create cell renderer to display date in 'Mon DD, YYYY' format
// ======================================================================

// Code from Oracle documentation
// https://docs.oracle.com/javase/tutorial/uiswing/components/table.html#renderer

import java.text.DateFormat;

import javax.swing.table.DefaultTableCellRenderer;

class DateRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1430088143813300885L;
	DateFormat formatter;

	public DateRenderer() {
		super();
	}

	public void setValue(Object value) {
		if (formatter == null) {
			formatter = DateFormat.getDateInstance();
		}
		setText((value == null) ? "" : formatter.format(value));
	}
}