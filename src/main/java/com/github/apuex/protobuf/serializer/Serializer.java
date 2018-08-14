package com.github.apuex.protobuf.serializer;

import com.google.protobuf.Message;

public interface Serializer {
  byte[] toBinary(Message obj);

  Message fromBinary(byte[] bytes);
}
