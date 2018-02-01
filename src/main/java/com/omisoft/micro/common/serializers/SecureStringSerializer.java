package com.omisoft.micro.common.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.omisoft.micro.common.structures.SecureString;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecureStringSerializer extends StdSerializer<SecureString> {

  public SecureStringSerializer() {
    this(null);
  }

  public SecureStringSerializer(Class<SecureString> t) {
    super(t);
  }


  @Override
  public void serialize(SecureString value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeStartArray();

    for (int i = 0; i < value.length(); i++) {
      jgen.writeString(value.toCharArray(), i, 1);
    }
    jgen.writeEndArray();
  }
}
