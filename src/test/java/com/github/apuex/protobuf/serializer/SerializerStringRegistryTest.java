package com.github.apuex.protobuf.serializer;

import org.junit.Test;

import static com.github.apuex.protobuf.serializer.TestData.*;


public class SerializerStringRegistryTest {
  @Test
  public void testCodec1() throws Exception {
    verify(getSerializerWithStringRegistry(), getRegistry());
  }

}
