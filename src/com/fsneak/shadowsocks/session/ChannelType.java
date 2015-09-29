package com.fsneak.shadowsocks.session;

public enum ChannelType {
    LOCAL,
    REMOTE;

    public ChannelType getOpposite() {
        if (this == LOCAL) {
            return REMOTE;
        } else if (this == REMOTE) {
            return LOCAL;
        } else {
            throw new IllegalStateException("wrong code: " + this);
        }
    }
}