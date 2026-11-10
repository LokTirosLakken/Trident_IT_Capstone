package tridentIT;

public record Equipment(int id, String serial, String manu, String desc, String type, String condition) {
	@Override
	public String toString() {
		return "";
	}

	public Object[] toRow() {
		return (new Object[] { String.format("EQP-%05d", id), desc, type, condition, "date" });
	}
}
