package net.darktree;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

// move to the .net package
public class RelayIdentifier {

	public static final short DEFAULT_PORT = 9686;

	private final String address;
	private final short port;

	public RelayIdentifier(String address, short port) {
		this.address = address;
		this.port = port;
	}

	public static Optional<RelayIdentifier> tryParse(String address) {
		if (address == null || address.isEmpty()) {
			return Optional.empty();
		}

		if (address.contains(":")) {
			String[] parts = address.split(":");

			if (parts.length != 2) {
				return Optional.empty();
			}

			try {
				short port = Short.parseShort(parts[1]);
				return Optional.of(new RelayIdentifier(parts[0], port));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}

		return Optional.of(new RelayIdentifier(address, DEFAULT_PORT));
	}

	public String getAddress() {
		return address;
	}

	public short getPort() {
		return port;
	}

	public Socket openConnection() throws IOException {
		return new Socket(address, port);
	}

	@Override
	public String toString() {
		return address + ":" + port;
	}

	public static RelayIdentifier getDefault(String address) {
		return new RelayIdentifier(address, DEFAULT_PORT);
	}

}
