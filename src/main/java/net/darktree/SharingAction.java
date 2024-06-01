package net.darktree;

public enum SharingAction {

	JOIN("Join"),
	HOST("Share"),
	LEAVE("Leave");

	private final String label;

	SharingAction(String label) {
		this.label = label;
	}

	String getLabel() {
		return label;
	}

}
