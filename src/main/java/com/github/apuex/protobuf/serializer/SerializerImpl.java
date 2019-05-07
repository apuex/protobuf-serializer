package com.github.apuex.protobuf.serializer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class SerializerImpl implements Serializer {
  private final ParserRegistry registry;

  public SerializerImpl(final ParserRegistry registry) {
    this.registry = registry;
  }

  @Override
  public ByteString toBinary(Message obj) {
    return Envelope.newBuilder()
        .setMessageMeta(registry.getMetaData(obj.getClass().getName()))
        .setMessageBody(obj.toByteString())
        .build()
        .toByteString();
  }

  @Override
  public Message fromBinary(ByteString bytes) {
    try {
      Envelope envelope = Envelope.parseFrom(bytes);
      return registry.getParser(envelope.getMessageMeta())
          .parseFrom(envelope.getMessageBody());
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  public Registry getRegistry() {
    return registry.getRegistry();
  }
}
