package net.darktree.urp;

@Deprecated(forRemoval = true)
public class NetUtils {

	@Deprecated(forRemoval = true)
	public static int readIntLE(byte[] data, int offset) {
		return ((data[offset + 3] & 0xFF) << 24) + ((data[offset + 2] & 0xFF) << 16) + ((data[offset + 1] & 0xFF) << 8) + ((data[offset] & 0xFF) << 0);
	}

	@Deprecated(forRemoval = true)
	public static void writeIntLE(byte[] data, int offset, int value) {
		data[offset] = (byte)(value >> 0);
		data[offset + 1] = (byte)(value >> 8);
		data[offset + 2] = (byte)(value >> 16);
		data[offset + 3] = (byte)(value >> 24);
	}

}
