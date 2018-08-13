package com.github.apuex.protobuf.serializer;

import com.google.protobuf.Message;

public interface Serializer {
	public byte[] toBinary(Message obj);
	public Message fromBinary(byte[] bytes);
}
