package yetAnotherConnect4;

public enum Player {
	RED,
	BLUE,
	NONE;
	
	public boolean isRed() {
		return this.equals(RED);
	}
	public boolean isBlue() {
		return this.equals(BLUE);
	}
	public boolean isNone() {
		return this.equals(NONE);
	}
	public boolean isPlayer() {
		return !this.isNone();
	}
	
	public Player next() {
		switch (this) {
		case BLUE:
			return RED;
		case RED:
			return BLUE;
		default:
			return NONE;
		}
	}
}
