package com.github.apuex.protobuf.serializer;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.github.apuex.protobuf.serializer.TestData.*;


public class AnyPackagerTest {
  @Test
  public void testPack() throws Exception {
    AnyPackager packager = AnyPackagerBuilder.builder()
        .withFileDescriptorProto(WireFormat.getDescriptor().toProto())
        .withStringRegistry()
        .build();
    Register register  = Register.newBuilder()
        .addQualifiedNames("name1")
        .addQualifiedNames("name2")
        .build();

    Any any = packager.pack(register);
    Message message = packager.unpack(any);
    assertEquals("parsed should be equal to the original", register, message);
    assertEquals("parsed should be equal to the original", register.getClass(), message.getClass());
    assertEquals("parsed should be equal to the original", register.getClass().getName(), message.getClass().getName());
  }

}
