package com.github.apuex.protobuf.serializer;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.util.HashMap;
import java.util.Map;

public class SerializerBuilder {
  private Map<String, Parser<? extends Message>> msgParsers;
  private ParserRegistry registry;

  public SerializerBuilder() {
    this.msgParsers = new HashMap<>();
  }

  public static SerializerBuilder builder() {
    return new SerializerBuilder();
  }

  public SerializerBuilder withParsers(Map<String, Parser<? extends Message>> msgParsers) {
    this.msgParsers.putAll(msgParsers);
    return this;
  }

  public SerializerBuilder withFileDescriptorProto(FileDescriptorProto fdp) {
    String packageName = fdp.getOptions().getJavaPackage();
    Boolean multipleFiles = fdp.getOptions().getJavaMultipleFiles();
    String outerClassName = multipleFiles ? "" : fdp.getOptions().getJavaOuterClassname() + "$";
    for (DescriptorProto dp : fdp.getMessageTypeList()) {
      String className = String.format("%s.%s%s", packageName, outerClassName, dp.getName());
      @SuppressWarnings("unchecked")
      Class<Message> clazz = null;
      try {
        clazz = ((Class<Message>) Class.forName(className));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
      Message defaultInstance = Internal.getDefaultInstance(clazz);

      msgParsers.put(className, defaultInstance.getParserForType());
    }
    return this;
  }

  public SerializerBuilder withIntegerRegistry() {
    registry = new ParserRegistryIntegerImpl();
    return this;
  }

  public SerializerBuilder withStringRegistry() {
    registry = new ParserRegistryStringImpl();
    return this;
  }

  public Serializer build() {
    registry.register(msgParsers);
    return new SerializerImpl(registry);
  }

  public Map<String, Parser<? extends Message>> getMsgParsers() {
    return msgParsers;
  }

  public ParserRegistry getRegistry() {
    return registry;
  }
}
