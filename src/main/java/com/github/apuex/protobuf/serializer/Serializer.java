package com.github.apuex.protobuf.serializer;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

public interface Serializer {
  ByteString toBinary(Message obj);

  Message fromBinary(ByteString bytes);
}
