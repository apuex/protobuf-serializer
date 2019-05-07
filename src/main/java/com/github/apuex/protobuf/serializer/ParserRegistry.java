package com.github.apuex.protobuf.serializer;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.util.Map;

public interface ParserRegistry {
  Parser<? extends Message> getParser(String clazz);
  Parser<? extends Message> getParser(ByteString metaData);

  ByteString getMetaData(String clazz);

  Registry register(Map<String, Parser<? extends Message>> msgParsers);

  Registry getRegistry();
}
