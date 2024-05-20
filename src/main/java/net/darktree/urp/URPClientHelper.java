package net.darktree.urp;

public class URPClientHelper {
    protected enum R2U {
        WELC(0x10),
        MADE(0x12),
        JOIN(0x13),
        LEFT(0x14),
        STAT(0x15),
        VALS(0x16),
        TEXT(0x11);

        private final int value;

        R2U(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        static R2U fromInt(int value) {
            for (R2U type : R2U.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return null;
        }
    }

    protected String roleToString(byte role) {
        switch (role) {
            case 1:
                return "connected";
            case 2:
                return "member";
            case 4:
                return "host";
            default:
                return "<invalid value>";
        }
    }

    protected String madeCodeToString(byte code, int gid) {
        if (code == 0x00) return "Created group #" + gid;
        if (code == 0x10) return "Joined group #" + gid;

        if ((code & 0xF0) > 0) {
            return "Failed to join the given group!\n";
        } else {
            return "Failed to create group!\n";
        }
    }
}
