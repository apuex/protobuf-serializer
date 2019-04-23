package com.github.apuex.protobuf.serializer;

import org.junit.Test;

import static com.github.apuex.protobuf.serializer.TestData.*;


public class SerializerIntegerRegistryTest {
  @Test
  public void testCodec1() throws Exception {
    verify(getSerializerWithIntegerRegistry(), getRegistry());
  }
}
