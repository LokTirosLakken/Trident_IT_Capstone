package tridentIT;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public record Equipment(int id, String serial, String manu, String desc, String type, String condition, Date dateAdded) {
	@Override
	public String toString() {
		return ""; // Add string format here after all fields are decided
	}

	// Format object as array of object for inserting as row to table
	public Object[] toRow() {
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM); // I despise date formatting
		return (new Object[] { String.format("EQP-%05d", id), desc, type, condition, dateAdded.toLocalDate().format(formatter) });
	}
}
