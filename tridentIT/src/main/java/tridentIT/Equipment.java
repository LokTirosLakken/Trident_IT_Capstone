package tridentIT;

import java.sql.Date;
import java.time.format.DateTimeFormatter;

public record Equipment(int id, String serial, String manu, String desc, String type, String condition, Date dateAdded) {
	@Override
	public String toString() {
		return "";
	}

	public Object[] toRow() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("");
		return (new Object[] { String.format("EQP-%05d", id), desc, type, condition, dateAdded.toLocalDate() });
	}
}
