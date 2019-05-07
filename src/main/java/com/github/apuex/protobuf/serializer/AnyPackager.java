package com.github.apuex.protobuf.serializer;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class AnyPackager {
  private final ParserRegistry registry;

  public AnyPackager(final ParserRegistry registry) {
    this.registry = registry;
  }

  public Any pack(Message obj) {
    return Any.pack(obj);
  }

  public Message unpack(Any any) {
    try {
      String[] types = any.getTypeUrl().split("/");
      return registry.getParser(types[types.length - 1])
          .parseFrom(any.getValue());
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  public Registry getRegistry() {
    return registry.getRegistry();
  }
}
