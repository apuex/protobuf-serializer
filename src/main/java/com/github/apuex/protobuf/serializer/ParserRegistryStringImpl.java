package com.github.apuex.protobuf.serializer;

import com.google.protobuf.*;

import java.util.HashMap;
import java.util.Map;

public class ParserRegistryStringImpl implements ParserRegistry {
  private final Map<ByteString, Parser<? extends Message>> msgParsers;

  public ParserRegistryStringImpl() {
    this.msgParsers = new HashMap<>();
    this.msgParsers.put(StringValue.of(Registry.class.getName()).toByteString(), Registry.getDefaultInstance().getParserForType());
    this.msgParsers.put(StringValue.of(Register.class.getName()).toByteString(), Register.getDefaultInstance().getParserForType());
  }

  @Override
  public Registry register(final Map<String, Parser<? extends Message>> msgParsers) {
    Map<String, ByteString> registered = new HashMap<>();

    msgParsers.forEach((k, v) -> {
      if (!Register.class.getName().equals(k) && !Registry.class.getName().equals(k)) {
        this.msgParsers.put(StringValue.of(k).toByteString(), v);
        registered.put(k, StringValue.of(k).toByteString());
      } else {
      }
    });

    return Registry.newBuilder()
        .putAllMetaData(registered)
        .build();
  }

  public Registry merge(Registry registry) {
    Map<String, ByteString> registered = new HashMap<>();
    registry.getMetaDataMap().forEach((k, v) -> {
      if (!Register.class.getName().equals(k) && !Registry.class.getName().equals(k)) {
        try {
          @SuppressWarnings("unchecked")
          Class<Message> clazz = ((Class<Message>) Class.forName(k));
          Message defaultInstance = com.google.protobuf.Internal.getDefaultInstance(clazz);
          this.msgParsers.put(v, defaultInstance.getParserForType());
          registered.put(k, v);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      } else {
      }
    });

    return Registry.newBuilder()
        .putAllMetaData(registered)
        .build();
  }

  @Override
  public Registry getRegistry() {
    Map<String, ByteString> registered = new HashMap<>();
    this.msgParsers.forEach((k, v) -> {
      try {
        registered.put(StringValue.parseFrom(k).getValue(), k);
      } catch (InvalidProtocolBufferException e) {
        throw new RuntimeException(e);
      }
    });
    return Registry.newBuilder()
        .putAllMetaData(registered)
        .build();
  }

  @Override
  public Parser<? extends Message> getParser(ByteString metaData) {
    return msgParsers.get(metaData);
  }

  @Override
  public ByteString getMetaData(String clazz) {
    ByteString metaData = StringValue.of(clazz).toByteString();
    if (this.msgParsers.containsKey(metaData)) {
      return metaData;
    } else {
      return ByteString.copyFrom(new byte[]{});
    }
  }
}
