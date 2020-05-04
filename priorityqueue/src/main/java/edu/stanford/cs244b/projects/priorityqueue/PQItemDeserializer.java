package edu.stanford.cs244b.projects.priorityqueue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.apache.kafka.common.serialization.Deserializer;


public class PQItemDeserializer implements Deserializer<PQItem> {

  @Override
  public PQItem deserialize(String topic, byte[] data) {
    try {
      DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));

      long priority = dataInputStream.readLong();
      long uuidLSB = dataInputStream.readLong();
      long uuidMSB = dataInputStream.readLong();
      byte[] message = new byte[data.length - 3*Long.BYTES];
      dataInputStream.read(message);

      return new PQItem(priority, new UUID(uuidMSB, uuidLSB), message);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
