package com.xiezhiheng.shadowsocks.socks5;

import java.nio.ByteBuffer;

/**
 * @author xiezhiheng
 */
public interface Socks5StageHandler {
	/**
	 * 根据阶段处理buffer中的数据，并给出相应的返回。
	 * 传入的buffer应该是可以读取的状态。如果读取后要把buffer改为继续写入的状态，要在这个方法之外处理。
	 * @param buffer
	 * @return
	 */
	Socks5HandleResult handle(ByteBuffer buffer);
}
