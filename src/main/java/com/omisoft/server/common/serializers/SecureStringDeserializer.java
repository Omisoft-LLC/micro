package com.omisoft.server.common.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.omisoft.server.common.structures.SecureString;

import java.io.IOException;

/**
 * Created by dido on 12/7/16.
 */
public class SecureStringDeserializer extends StdDeserializer<SecureString> {
  protected SecureStringDeserializer(Class<?> vc) {
    super(vc);
  }

  public SecureStringDeserializer() {
    this(null);
  }

  @Override
  public SecureString deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {

    Character[] characterArray = jp.getCodec().readValue(jp, Character[].class);
    char[] charBuffer = new char[characterArray.length];
    int i = 0;
    for (Character c : characterArray) {
      charBuffer[i++] = c;
    }

    return new SecureString(charBuffer);
  }
}
