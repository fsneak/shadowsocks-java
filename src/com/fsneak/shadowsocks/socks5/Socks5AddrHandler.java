package com.fsneak.shadowsocks.socks5;

import com.fsneak.shadowsocks.log.Logger;

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
public class Socks5AddrHandler implements Socks5StageHandler {
	private static final byte CMD_CONNECT = 0x01;
	private static final byte CMD_BIND = 0x02;
	private static final byte CMD_UDP_ASSOC = 0x03;

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

		return null;
	}
}
