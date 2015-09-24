package com.fsneak.shadowsocks.socks5;

import com.fsneak.shadowsocks.log.Logger;

import java.net.*;
import java.nio.ByteBuffer;

/**
 The SOCKS request is formed as follows:

 +----+-----+-------+------+----------+----------+
 |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
 +----+-----+-------+------+----------+----------+
 | 1  |  1  | X'00' |  1   | Variable |    2     |
 +----+-----+-------+------+----------+----------+

 Where:

 o  VER    protocol version: X'05'
 o  CMD
	 o  CONNECT X'01'
	 o  BIND X'02'
	 o  UDP ASSOCIATE X'03'
 o  RSV    RESERVED
 o  ATYP   address type of following address
	 o  IP V4 address: X'01'
	 o  DOMAINNAME: X'03'
	 o  IP V6 address: X'04'
 o  DST.ADDR       desired destination address
 o  DST.PORT desired destination port in network octet order

 In an address field (DST.ADDR, BND.ADDR), the ATYP field specifies
 the type of address contained within the field:

 o  X'01'

 the address is a version-4 IP address, with a length of 4 octets

 o  X'03'

 the address field contains a fully-qualified domain name.  The first
 octet of the address field contains the number of octets of name that
 follow, there is no terminating NUL octet.

 o  X'04'

 the address is a version-6 IP address, with a length of 16 octets.


 * @author xiezhiheng
 */
public class Socks5AddressHandler implements Socks5StageHandler {
	private static final Socks5AddressHandler INSTANCE = new Socks5AddressHandler();

	private static final byte CMD_CONNECT = 0x01;
	private static final byte CMD_BIND = 0x02;
	private static final byte CMD_UDP_ASSOC = 0x03;

	private static final byte ATYP_IPV4 = 0x01;
	private static final byte ATYP_DOMAINNAME = 0x03;
	private static final byte ATYP_IPV6 = 0x04;

	private static final byte[] SOCKS5_ADDR_RESPONSE = {0x05, 0, 0, 0x01, 0, 0, 0, 0, 0x10, 0x10};

	private Socks5AddressHandler() {
	}

	public static Socks5AddressHandler getInstance() {
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

		byte cmd = buffer.get();
		if (cmd != CMD_CONNECT) {
			Logger.error("socks5 unsupported cmd: " + cmd);
			return new Socks5HandleResult(Socks5HandleResult.Type.ERROR);
		}

		buffer.get(); // skip the reserved byte
		// check if all data received
		int pos = buffer.position();
		byte addressType = buffer.get();
		SocketAddress address;
		try {
			switch (addressType) {
				case ATYP_IPV4:
					address = parseIpV4(buffer);
					break;
				case ATYP_DOMAINNAME:
					address = parseDomainName(buffer);
					break;
				case ATYP_IPV6:
					address = parseIpV6(buffer);
					break;
				default:
					Logger.error("socks5 unexpected address type: " + addressType);
					return new Socks5HandleResult(Socks5HandleResult.Type.ERROR);
			}
		} catch (UnknownHostException e) {
			Logger.error(e);
			return new Socks5HandleResult(Socks5HandleResult.Type.ERROR);
		}


		if (address == null) {
			return new Socks5HandleResult(Socks5HandleResult.Type.UNCOMPLETED);
		}

		// full data has been received
		Logger.info("connect to " + address);
		buffer.position(pos);
		ByteBuffer copyBuffer = ByteBuffer.allocate(buffer.remaining());
		copyBuffer.put(buffer).flip();

		return new Socks5HandleResult(Socks5HandleResult.Type.COMPLETED,
				ByteBuffer.wrap(SOCKS5_ADDR_RESPONSE), copyBuffer);
	}

	private SocketAddress parseIpV4(ByteBuffer buffer) throws UnknownHostException {
		if (buffer.remaining() < 6) {
			return null;
		}
		byte[] ip = new byte[4];
		buffer.get(ip);
		int port = buffer.getShort();
		return new InetSocketAddress(InetAddress.getByAddress(ip), port);
	}

	private SocketAddress parseDomainName(ByteBuffer buffer) {
		int len = Socks5Utils.getByteLen(buffer);
		if (buffer.remaining() < len + 2) {
			return null;
		}

		byte[] domainName = new byte[len];
		buffer.get(domainName);
		int port = buffer.getShort();
		return new InetSocketAddress(new String(domainName), port);
	}

	private SocketAddress parseIpV6(ByteBuffer buffer) throws UnknownHostException {
		if (buffer.remaining() < 18) {
			return null;
		}

		byte[] ip = new byte[16];
		buffer.get(ip);
		int port = buffer.getShort();
		return new InetSocketAddress(InetAddress.getByAddress(ip), port);
	}
}
