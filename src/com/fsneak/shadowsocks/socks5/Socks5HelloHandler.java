package com.fsneak.shadowsocks.socks5;

import com.fsneak.shadowsocks.log.Logger;

import java.nio.ByteBuffer;

/**
 The client connects to the server, and sends a version
 identifier/method selection message:

 +----+----------+----------+
 |VER | NMETHODS | METHODS  |
 +----+----------+----------+
 | 1  |    1     | 1 to 255 |
 +----+----------+----------+

 The VER field is set to X'05' for this version of the protocol.  The
 NMETHODS field contains the number of method identifier octets that
 appear in the METHODS field.

 The server selects from one of the methods given in METHODS, and
 sends a METHOD selection message:

 +----+--------+
 |VER | METHOD |
 +----+--------+
 | 1  |   1    |
 +----+--------+

 If the selected METHOD is X'FF', none of the methods listed by the
 client are acceptable, and the client MUST close the connection.

 The values currently defined for METHOD are:

 o  X'00' NO AUTHENTICATION REQUIRED
 o  X'01' GSSAPI
 o  X'02' USERNAME/PASSWORD
 o  X'03' to X'7F' IANA ASSIGNED
 o  X'80' to X'FE' RESERVED FOR PRIVATE METHODS
 o  X'FF' NO ACCEPTABLE METHODS
 * @author xiezhiheng
 */
public class Socks5HelloHandler implements  Socks5StageHandler {
	private static final Socks5HelloHandler INSTANCE = new Socks5HelloHandler();

	private static final byte[] RESPONSE_TO_LOCAL = {5, 0};

	private Socks5HelloHandler() {
	}

	public static Socks5HelloHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public Socks5HandleResult handle(ByteBuffer buffer) {
		if (buffer.remaining() < 3) {
			return new Socks5HandleResult(Socks5HandleResult.Type.UNCOMPLETED);
		}

		byte version = buffer.get();
		if (!Socks5Utils.verifyVersion(version)) {
			Logger.error("socks5 hello stage wrong version: " + version);
			return new Socks5HandleResult(Socks5HandleResult.Type.ERROR);
		}

		int len = Socks5Utils.getByteLen(buffer);
		if (buffer.remaining() < len) {
			return new Socks5HandleResult(Socks5HandleResult.Type.UNCOMPLETED);
		}

		// skip
		buffer.position(buffer.position() + len);
		return new Socks5HandleResult(Socks5HandleResult.Type.COMPLETED,
				ByteBuffer.wrap(RESPONSE_TO_LOCAL), null);
	}

	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[]{5, (byte) 200, 0});
		while (buffer.hasRemaining()) {
			int val = 0xff & buffer.get();
			System.out.println(val);
		}
		System.out.println(0x05);
	}
}
