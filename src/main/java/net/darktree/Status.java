package net.darktree;

public enum Status {
    OFFLINE,
    IN_GROUP,
    HOST;

    public boolean isConnected() {
        return this != OFFLINE;
    }
}
