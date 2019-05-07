package com.github.apuex.protobuf.serializer;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.util.HashMap;
import java.util.Map;

public class AnyPackagerBuilder {
  private Map<String, Parser<? extends Message>> msgParsers;
  private ParserRegistry registry;

  public AnyPackagerBuilder() {
    this.msgParsers = new HashMap<>();
  }

  public static AnyPackagerBuilder builder() {
    return new AnyPackagerBuilder();
  }

  public AnyPackagerBuilder withParsers(Map<String, Parser<? extends Message>> msgParsers) {
    this.msgParsers.putAll(msgParsers);
    return this;
  }

  public AnyPackagerBuilder withFileDescriptorProto(FileDescriptorProto fdp) {
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

  public AnyPackagerBuilder withIntegerRegistry() {
    registry = new ParserRegistryIntegerImpl();
    return this;
  }

  public AnyPackagerBuilder withStringRegistry() {
    registry = new ParserRegistryClassNameImpl();
    return this;
  }

  public AnyPackager build() {
    registry.register(msgParsers);
    return new AnyPackager(registry);
  }

  public Map<String, Parser<? extends Message>> getMsgParsers() {
    return msgParsers;
  }

  public ParserRegistry getRegistry() {
    return registry;
  }
}
