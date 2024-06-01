package net.darktree.urp;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import net.darktree.RelayIdentifier;

import java.net.Socket;

public class UrpClient {

	public final static int MAKE_SUCCESS = 0x00;
	public final static int JOIN_SUCCESS = 0x10;

	private Socket socket = null;
	private LittleEndianDataInputStream dataIn = null;
	private LittleEndianDataOutputStream dataOut = null;
	private Thread serverListenerThread = null;
	private boolean connected = false;
	private int uid = -1;
	private PacketReader r2u;
	private PacketWriter writer;

	public UrpClient(RelayIdentifier address) {
		try {
			socket = new Socket(address.getAddress(), address.getPort());
			dataIn = new LittleEndianDataInputStream(socket.getInputStream());
			dataOut = new LittleEndianDataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			System.out.println("Failed to open socket");
			return;
		}

		System.out.println("Successfully made connection to " + address);

		r2u = new PacketReader();

		// start listening for server messages
		serverListenerThread = new Thread(this::serverListener);
		serverListenerThread.start();

		writer = new PacketWriter(dataOut);
	}

	public boolean isConnected() {
		return connected;
	}

	public int getUid() {
		return uid;
	}

	public PacketReader getRxBuffer(){
		return r2u;
	}

	public PacketWriter getTxBuffer(){
		return writer;
	}

	public void waitForConnection(int timeout) {
		long start = System.currentTimeMillis();
		while (!connected) {
			try {
				Thread.sleep(10);
				if (System.currentTimeMillis() - start > timeout) {
					System.out.println("Connection timeout");
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void serverListener() {
		System.out.println("Listening for server messages");

		try {
			while (socket.isConnected() && !serverListenerThread.isInterrupted()) {

				int id = dataIn.readByte();

				switch (id) {

					case UrpMessage.R2U_WELC -> {
						int ver = dataIn.readShort();
						int rev = dataIn.readShort();
						uid = dataIn.readInt();

						byte[] message = new byte[65];
						dataIn.read(message, 0, 64);
						message[64] = 0;

						// trim message to remove null bytes
						int messageLength = 0;
						for (int i = 0; i < 64; i++) {
							if (message[i] == 0) {
								messageLength = i;
								break;
							}
						}
						byte[] messageTrimmed = new byte[messageLength];
						System.arraycopy(message, 0, messageTrimmed, 0, messageLength);

						System.out.println("Received URP welcome, using protocol v" + ver + "." + rev + " (uid: " + uid + ")");
						System.out.println("Server identifies as: " + new String(messageTrimmed));

						r2u.addMessage(new UrpMessage(UrpMessage.R2U_WELC, -1, new byte[]{(byte) ver, (byte) rev}));

						connected = true;
					}

					case UrpMessage.R2U_STAT -> {
						byte sta = dataIn.readByte();
						System.out.println("Your status changed to: " + roleToString(sta));

						r2u.addMessage(new UrpMessage(UrpMessage.R2U_STAT, -1, new byte[]{sta}));
					}

					case UrpMessage.R2U_JOIN -> {
						int joinUid = dataIn.readInt();
						System.out.println("User #" + joinUid + " joined");

						r2u.addMessage(new UrpMessage(UrpMessage.R2U_JOIN, joinUid, new byte[0]));
					}

					case UrpMessage.R2U_LEFT -> {
						int leftUid = dataIn.readInt();
						System.out.println("User #" + leftUid + " left");

						r2u.addMessage(new UrpMessage(UrpMessage.R2U_LEFT, leftUid, new byte[0]));
					}

					case UrpMessage.R2U_VALS -> {
						int key = dataIn.readInt();
						int val = dataIn.readInt();
						System.out.println("Setting '" + key + "' is set to '" + val + "'");

						byte[] valBuffer = new byte[8];
						NetUtils.writeIntLE(valBuffer, 0, val);
						NetUtils.writeIntLE(valBuffer, 4, key);
						r2u.addMessage(new UrpMessage(UrpMessage.R2U_VALS, -1, valBuffer));
					}

					case UrpMessage.R2U_MADE -> {
						byte madeStatus = dataIn.readByte();
						int madeGid = dataIn.readInt();
						System.out.println(madeCodeToString(madeStatus, madeGid));

						byte[] madeBuffer = new byte[5];
						NetUtils.writeIntLE(madeBuffer, 0, madeGid);
						madeBuffer[4] = madeStatus;
						r2u.addMessage(new UrpMessage(UrpMessage.R2U_MADE, -1, madeBuffer));
					}

					case UrpMessage.R2U_TEXT -> {
						int textUid = dataIn.readInt();
						int textLen = dataIn.readInt();

						byte[] textBuffer = new byte[textLen + 1];
						dataIn.read(textBuffer, 0, textLen);
						textBuffer[textLen] = 0;
						System.out.println("User #" + textUid + " said: '" + new String(textBuffer) + "'");

						r2u.addMessage(new UrpMessage(UrpMessage.R2U_TEXT, textUid, textBuffer));
					}

					default -> System.out.println("Unknown message id: " + id);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connected = false;
		}
	}

	public void close() {
		try {
			serverListenerThread.interrupt();
			dataIn.close();
			dataOut.close();
			socket.close();
			System.out.println("Closed connection");
		} catch (Exception e) {
			System.out.println("Failed to close");
		}
	}

	public static String madeCodeToString(byte code, int gid) {
		if (code == 0x00) return "Created group #" + gid;
		if (code == 0x10) return "Joined group #" + gid;

		if ((code & 0xF0) > 0) {
			return "Failed to join the given group!\n";
		} else {
			return "Failed to create group!\n";
		}
	}

	public static String roleToString(byte role) {
		return switch (role) {
			case 1 -> "connected";
			case 2 -> "member";
			case 4 -> "host";
			default -> "<invalid value>";
		};
	}

}
