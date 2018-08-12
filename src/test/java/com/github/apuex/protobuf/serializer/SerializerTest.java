package com.github.apuex.protobuf.serializer;

import static com.github.apuex.protobuf.serializer.WireFormat.*;
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
		Assert.assertEquals(String.format("%s != %s", registry, message), registry, message);
	}

	private Serializer serializer() throws Exception {
		Map<String, Parser<? extends Message>> msgParsers = new HashMap<>();

		FileDescriptorProto fdp = WireFormat.getDescriptor().toProto();
		String packageName = fdp.getOptions().getJavaPackage();
		Boolean multipleFiles = fdp.getOptions().getJavaMultipleFiles();
		String outerClassName = multipleFiles ? "" : fdp.getOptions().getJavaOuterClassname() + "$";
		for (DescriptorProto dp : fdp.getMessageTypeList()) {
			String className = String.format("%s.%s%s", packageName, outerClassName, dp.getName());
			@SuppressWarnings("unchecked")
			Class<Message> clazz = ((Class<Message>) Class.forName(className));
			Message defaultInstance = com.google.protobuf.Internal.getDefaultInstance(clazz);

			msgParsers.put(className, defaultInstance.getParserForType());
		}
		Serializer serializer = new Serializer(msgParsers);
		return serializer;
	}
}
