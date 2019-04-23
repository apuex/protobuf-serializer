package com.github.apuex.protobuf.serializer;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static javafx.fxml.FXMLLoader.DEFAULT_CHARSET_NAME;

public class TestData {
  public static void verify(Serializer serializer, Registry registry) {
    Message message = serializer.fromBinary(serializer.toBinary(registry));
    Assert.assertEquals(
        String.format("%s != %s", registry.getClass().getName(), message.getClass().getName()),
        registry.getClass().getName(),
        message.getClass().getName());
    Assert.assertEquals(String.format("%s != %s", registry, message), registry, message);
  }

  public static Registry getRegistry() throws UnsupportedEncodingException {
    Map<String, ByteString> values = new HashMap<>();
    values.put("hello", ByteString.copyFrom("world", DEFAULT_CHARSET_NAME));
    return Registry.newBuilder().putAllMetaData(values).build();
  }

  public static Serializer getSerializerWithStringRegistry() {
    return SerializerBuilder.builder()
        .withFileDescriptorProto(WireFormat.getDescriptor().toProto())
        .withStringRegistry()
        .build();
  }

  public static Serializer getSerializerWithIntegerRegistry() {
    return SerializerBuilder.builder()
        .withFileDescriptorProto(WireFormat.getDescriptor().toProto())
        .withIntegerRegistry()
        .build();
  }
}
