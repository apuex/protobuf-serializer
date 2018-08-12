package com.github.apuex.protobuf.serializer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

public class SerializerTest {

	@Test
	public void testCodec1() throws Exception {
		Map<String, ByteString> values = new HashMap<>();
		values.put("hello", ByteString.copyFrom("world", "utf8"));
		Registry registry = Registry.newBuilder().putAllMetaData(values).build();

		Serializer serializer = serializer();
		Message message = serializer.fromBinary(serializer.toBinary(registry));
		Assert.assertTrue(String.format("%s != %s", registry, message), registry.equals(message));
	}

	private Serializer serializer() throws Exception {
		Map<String, Parser<? extends Message>> msgDescriptors = new HashMap<>();

		FileDescriptorProto fdp = WireFormat.getDescriptor().toProto();
		String packageName = fdp.getOptions().getJavaPackage();
		for (DescriptorProto dp : fdp.getMessageTypeList()) {
			String className = String.format("%s.%s", packageName, dp.getName());
			@SuppressWarnings("unchecked")
			Class<Message> clazz = ((Class<Message>) Class.forName(className));
			Message defaultInstance = com.google.protobuf.Internal.getDefaultInstance(clazz);

			msgDescriptors.put(className, defaultInstance.getParserForType());
		}
		Serializer serializer = new Serializer(msgDescriptors);
		return serializer;
	}
}
