package net.darktree.urp;



public class R2UMessage {
    public enum R2U {
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

    private final R2U type;
    private final int fromUid;
    private final byte[] data;

    public R2UMessage(R2U type, int fromUid, byte[] data) {
        this.fromUid = fromUid;
        this.data = data;
        this.type = type;
    }

    public int getFromUid() {
        return fromUid;
    }

    public byte[] getData() {
        return data;
    }

    public R2U getType() {
        return type;
    }

    @Override
    public String toString() {
        return "R2UMessage{ fromUid=" + fromUid + ", data=" + new String(data) + '}';
    }
}
