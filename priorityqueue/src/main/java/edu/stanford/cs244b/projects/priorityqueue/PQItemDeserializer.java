package edu.stanford.cs244b.projects.priorityqueue;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;


public class PQItemDeserializer implements Deserializer<PQItem> {

  @Override
  public PQItem deserialize(String topic, byte[] data) {
    try {
      DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));

      return PQItem.read(dataInputStream);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
