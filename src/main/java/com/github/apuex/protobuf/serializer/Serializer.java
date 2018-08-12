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
	private final Map<ByteString, Parser<? extends Message>> msgParsers;
  private final Map<String, ByteString> metaData;

	public Serializer() {
		this.msgParsers = new HashMap<>();
		this.msgParsers.put(Int32Value.of(0).toByteString(), Registry.getDefaultInstance().getParserForType());
		this.msgParsers.put(Int32Value.of(1).toByteString(), Register.getDefaultInstance().getParserForType());
		this.metaData = new HashMap<>();
		this.metaData.put(Registry.class.getName(), Int32Value.of(0).toByteString());
		this.metaData.put(Register.class.getName(), Int32Value.of(1).toByteString());
	}

	public Serializer(Charset charset, final Map<String, Parser<? extends Message>> msgParsers) {
		this.msgParsers = new HashMap<>();
		this.msgParsers.put(Int32Value.of(0).toByteString(), Registry.getDefaultInstance().getParserForType());
		this.msgParsers.put(Int32Value.of(1).toByteString(), Register.getDefaultInstance().getParserForType());
		this.metaData = new HashMap<>();
		this.metaData.put(Registry.class.getName(), Int32Value.of(0).toByteString());
		this.metaData.put(Register.class.getName(), Int32Value.of(1).toByteString());
	}
	
	public Registry register(final Map<String, Parser<? extends Message>> msgParsers) {

		this.msgParsers.keySet().forEach(k -> { 
			int v;
			try {
				v = Int32Value.parseFrom(k).getValue();
  			if(v > key) key = v; 
			} catch (InvalidProtocolBufferException e) {
  			throw new RuntimeException(e);
			}
		});

		msgParsers.forEach((k, v) -> {
			if (!Register.class.getName().equals(k) && !Registry.class.getName().equals(k)) {
				final int currentKey = key++;
				this.msgParsers.put(Int32Value.of(currentKey).toByteString(), v);
				this.metaData.put(k, Int32Value.of(currentKey).toByteString());
			} else {
				System.out.printf("%s is skipped.\n", k);
			}
		});
		
		return Registry.newBuilder()
				.putAllMetaData(this.metaData)
				.build();
	}

	public Registry merge(Registry registry) {
		registry.getMetaDataMap().forEach((k, v) -> {
			if (!Register.class.getName().equals(k) && !Registry.class.getName().equals(k)) {
				try {
    			@SuppressWarnings("unchecked")
    			Class<Message> clazz = ((Class<Message>) Class.forName(k));
					Message defaultInstance = com.google.protobuf.Internal.getDefaultInstance(clazz);
				  this.msgParsers.put(v, defaultInstance.getParserForType());
  				this.metaData.put(k, v);
				  int currentKey = Int32Value.parseFrom(v).getValue();
  			  if(currentKey > key) key = currentKey; 
				} catch (Exception e) {
    			throw new RuntimeException(e);
				}
			} else {
				System.out.printf("%s is skipped.\n", k);
			}
		});
		
		return Registry.newBuilder()
				.putAllMetaData(this.metaData)
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
			return msgParsers.get(envelope.getMessageMeta())
					.parseFrom(envelope.getMessageBody());
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}
	}
}
