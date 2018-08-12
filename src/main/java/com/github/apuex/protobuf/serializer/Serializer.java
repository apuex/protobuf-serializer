package com.github.apuex.protobuf.serializer;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

public class Serializer {
	public static final String DEFAULT_CHARSET_NAME = "utf8";
	private int key = 2;
	private final Charset charset;
	private final Map<Integer, Parser<? extends Message>> msgParsers;
  private final Map<String, ByteString> metaData;

	public Serializer() {
		this.charset = Charset.forName(DEFAULT_CHARSET_NAME);
		this.msgParsers = new HashMap<>();
		this.msgParsers.put(0, Registry.getDefaultInstance().getParserForType());
		this.msgParsers.put(1, Register.getDefaultInstance().getParserForType());
		this.metaData = new HashMap<>();
		this.metaData.put(Registry.class.getName(), Int32Value.of(0).toByteString());
		this.metaData.put(Register.class.getName(), Int32Value.of(1).toByteString());
	}

	public Serializer(Charset charset, final Map<String, Parser<? extends Message>> msgParsers) {
		this.charset = charset;
		this.msgParsers = new HashMap<>();
		this.msgParsers.put(0, Registry.getDefaultInstance().getParserForType());
		this.msgParsers.put(1, Register.getDefaultInstance().getParserForType());
		this.metaData = new HashMap<>();
		this.metaData.put(Registry.class.getName(), Int32Value.of(0).toByteString());
		this.metaData.put(Register.class.getName(), Int32Value.of(1).toByteString());
	}
	
	public Registry register(final Map<String, Parser<? extends Message>> msgParsers) {

		this.msgParsers.keySet().forEach(k -> { if(k > key) key = k; });

		msgParsers.forEach((k, v) -> {
			if (Register.class.getName() != k || Registry.class.getName() != k) {
				final int currentKey = key++;
				this.msgParsers.put(currentKey, v);
				metaData.put(k, Int32Value.of(currentKey).toByteString());
			}
		});
		
		return Registry.newBuilder()
				.putAllMetaData(metaData)
				.build();
	}
	
	public byte[] toBinary(Message obj) {
  	return Envelope.newBuilder()
			.setMessageMeta(metaData.get(obj.getClass().getName()))
			.setMessageBody(obj.toByteString())
			.build()
			.toByteArray();
	}

	public Message fromBinary(byte[] bytes) {
		try {
			Envelope envelope = Envelope.parseFrom(bytes);
			Integer className = Int32Value.parseFrom(envelope.getMessageMeta()).getValue();
			return msgParsers.get(className)
					.parseFrom(envelope.getMessageBody());
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}
	}
}
