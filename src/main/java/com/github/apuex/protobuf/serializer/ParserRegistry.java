package com.github.apuex.protobuf.serializer;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

public interface ParserRegistry {
  Parser<? extends Message> getParser(ByteString metaData);
  ByteString getMetaData(String clazz);
  Registry getRegistry();
}
