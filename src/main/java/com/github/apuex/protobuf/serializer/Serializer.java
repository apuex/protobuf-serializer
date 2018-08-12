package com.github.apuex.protobuf.serializer;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

public class Serializer {
	private final Map<String, Parser<? extends Message>> msgDescriptors;

	public Serializer(final Map<String, Parser<? extends Message>> msgDescriptors) {
		this.msgDescriptors = msgDescriptors;
	}

	public byte[] toBinary(Message obj) {
		try {
			return Envelope.newBuilder()
					.setMsgMeta(ByteString.copyFrom(obj.getClass().getName(), "utf8"))
					.setMessage(obj.toByteString())
					.build()
					.toByteArray();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public Message fromBinary(byte[] bytes) {
		try {
			Envelope envelope = Envelope.parseFrom(bytes);
			String className = envelope.getMsgMeta().toString("utf8");
			return msgDescriptors.get(className)
					.parseFrom(envelope.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}
	}
}
