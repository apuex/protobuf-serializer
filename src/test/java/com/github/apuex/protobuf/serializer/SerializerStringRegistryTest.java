package com.github.apuex.protobuf.serializer;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class SerializerStringRegistryTest {

  private static final String DEFAULT_CHARSET_NAME = "utf8";

  @Test
  public void testCodec1() throws Exception {
    SerializerImpl serializer = serializer();
    Map<String, ByteString> values = new HashMap<>();
    values.put("hello", ByteString.copyFrom("world", DEFAULT_CHARSET_NAME));
    Registry registry = Registry.newBuilder().putAllMetaData(values).build();

    byte[] binary = serializer.toBinary(registry);
    Message message = serializer.fromBinary(binary);
    Assert.assertEquals(
        String.format("%s != %s", registry.getClass().getName(), message.getClass().getName()),
        registry.getClass().getName(),
        message.getClass().getName());
    Assert.assertEquals(String.format("%s != %s", registry, message), registry, message);
  }

  @Test
  public void testCodec2() throws Exception {
    SerializerImpl serializer = serializer();
    Map<String, ByteString> values = new HashMap<>();
    values.put("hello", ByteString.copyFrom("world", DEFAULT_CHARSET_NAME));
    Registry registry = serializer.getRegistry();

    byte[] binary = serializer.toBinary(registry);
    Message message = serializer.fromBinary(binary);
    Assert.assertEquals(
        String.format("%s != %s", registry.getClass().getName(), message.getClass().getName()),
        registry.getClass().getName(),
        message.getClass().getName());
    Assert.assertEquals(String.format("%s != %s", registry, message), registry, message);
  }

  private SerializerImpl serializer() throws Exception {
    Map<String, Parser<? extends Message>> msgParsers = new HashMap<>();

    FileDescriptorProto fdp = WireFormat.getDescriptor().toProto();
    String packageName = fdp.getOptions().getJavaPackage();
    Boolean multipleFiles = fdp.getOptions().getJavaMultipleFiles();
    String outerClassName = multipleFiles ? "" : fdp.getOptions().getJavaOuterClassname() + "$";
    for (DescriptorProto dp : fdp.getMessageTypeList()) {
      String className = String.format("%s.%s%s", packageName, outerClassName, dp.getName());
      @SuppressWarnings("unchecked")
      Class<Message> clazz = ((Class<Message>) Class.forName(className));
      Message defaultInstance = Internal.getDefaultInstance(clazz);

      msgParsers.put(className, defaultInstance.getParserForType());
    }

    ParserRegistryStringImpl registry = new ParserRegistryStringImpl();
    SerializerImpl serializer = new SerializerImpl(registry);
    registry.register(msgParsers);

    return serializer;
  }

}
