package com.github.apuex.protobuf.serializer;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.github.apuex.protobuf.serializer.TestData.*;
import static org.junit.Assert.*;

public class SerializerBuilderTest {

  @Test
  public void builder() {
    SerializerBuilder builder = SerializerBuilder.builder();
    assertNotNull(builder);
    assertNotNull(builder.getMsgParsers());
    assertTrue(builder.getMsgParsers().isEmpty());
    assertNull(builder.getRegistry());
  }

  @Test
  public void withParsers() {
  }

  @Test
  public void withFileDescriptorProto() {
  }

  @Test
  public void withIntegerRegistry() {
  }

  @Test
  public void withStringRegistry() {
  }

  @Test
  public void build() throws UnsupportedEncodingException {
    verify(getSerializerWithIntegerRegistry(), getRegistry());
  }
}