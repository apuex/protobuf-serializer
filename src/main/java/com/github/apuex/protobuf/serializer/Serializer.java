package com.github.apuex.protobuf.serializer;

import java.nio.charset.Charset;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

public class Serializer {
	private final Charset charset;
	private final Map<String, Parser<? extends Message>> msgParsers;

	public Serializer(final Map<String, Parser<? extends Message>> msgParsers) {
		this.charset = Charset.forName("utf8");
		this.msgParsers = msgParsers;
	}

	public Serializer(Charset charset, final Map<String, Parser<? extends Message>> msgParsers) {
		this.charset = charset;
		this.msgParsers = msgParsers;
	}
	
	public byte[] toBinary(Message obj) {
  	return Envelope.newBuilder()
			.setMessageMeta(ByteString.copyFrom(obj.getClass().getName(), charset))
			.setMessageBody(obj.toByteString())
			.build()
			.toByteArray();
	}

	public Message fromBinary(byte[] bytes) {
		try {
			Envelope envelope = Envelope.parseFrom(bytes);
			String className = envelope.getMessageMeta().toString(charset);
			return msgParsers.get(className)
					.parseFrom(envelope.getMessageBody());
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}
	}
}
