package com.github.apuex.protobuf.serializer;

import com.google.protobuf.*;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class SerializerTest {

	private static final String DEFAULT_CHARSET_NAME = "utf8";

	@Test
	public void testCodec1() throws Exception {
		SerializerImpl serializer = serializer();
		Map<String, ByteString> values = new HashMap<>();
		values.put("hello", ByteString.copyFrom("world", DEFAULT_CHARSET_NAME));
		Registry registry = Registry.newBuilder().putAllMetaData(values).build();

		byte[] binary = serializer.toBinary(registry);
		print(binary);
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
		print(binary);
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
			Message defaultInstance = com.google.protobuf.Internal.getDefaultInstance(clazz);

			msgParsers.put(className, defaultInstance.getParserForType());
		}

		ParserRegistryImpl registry = new ParserRegistryImpl();
		SerializerImpl serializer = new SerializerImpl(registry);
		Registry registryValue = registry.register(msgParsers);
		registryValue.getMetaDataMap().forEach((key, value) -> {
			try {
				System.out.printf("%s => %d\n", key, Int32Value.parseFrom(value).getValue());
			} catch (InvalidProtocolBufferException e) {
				throw new RuntimeException(e);
			}
		});
		return serializer;
	}

	private void print(byte[] binary) throws Exception {
		System.out.println("binary:");
		for(byte b: binary) {
			System.out.printf("%02x ", 0xff&b);
		}
		System.out.println();
	}
}
