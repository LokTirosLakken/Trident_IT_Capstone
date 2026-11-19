package tridentIT;

// Create custom cell renderer for Equipment ID column

import javax.swing.table.DefaultTableCellRenderer;

class EquipIDRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 7709850063507507454L;

		public EquipIDRenderer() {
			super();
		}

		public void setValue(Object value) {
			setText((value == null) ? "" : String.format("EQP-%05d", (Integer) value));
		}
	}