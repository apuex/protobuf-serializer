package com.github.apuex.protobuf.serializer;

import com.google.protobuf.*;

import java.util.HashMap;
import java.util.Map;

public class ParserRegistryIntegerImpl implements ParserRegistry {
  private int key = 2;
  private final Map<ByteString, Parser<? extends Message>> msgParsers;
  private final Map<String, ByteString> metaData;

  public ParserRegistryIntegerImpl() {
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
        if (v > key) key = v;
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
          Message defaultInstance = Internal.getDefaultInstance(clazz);
          this.msgParsers.put(v, defaultInstance.getParserForType());
          this.metaData.put(k, v);
          int currentKey = Int32Value.parseFrom(v).getValue();
          if (currentKey > key) key = currentKey;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      } else {
      }
    });

    Map<String, ByteString> registered = new HashMap<>(metaData);
    registry.getMetaDataMap().forEach((key, value) -> registered.remove(key));
    return Registry.newBuilder()
        .putAllMetaData(registered)
        .build();
  }

  public Map<String, ByteString> getMetaData() {
    return metaData;
  }

  @Override
  public Registry getRegistry() {
    return Registry.newBuilder()
        .putAllMetaData(metaData)
        .build();
  }

  @Override
  public Parser<? extends Message> getParser(String clazz) {
    return msgParsers.get(getMetaData(clazz));
  }

  @Override
  public Parser<? extends Message> getParser(ByteString metaData) {
    return msgParsers.get(metaData);
  }

  @Override
  public ByteString getMetaData(String clazz) {
    return metaData.get(clazz);
  }
}
