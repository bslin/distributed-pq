package edu.stanford.cs244b.projects.priorityqueue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.kafka.common.serialization.Serializer;


public class PQItemSerializer implements Serializer<PQItem> {

  @Override
  public byte[] serialize(String topic, PQItem data) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

      dataOutputStream.writeLong(data.getKey().getPriority());
      dataOutputStream.writeLong(data.getKey().getUuid().getLeastSignificantBits());
      dataOutputStream.writeLong(data.getKey().getUuid().getMostSignificantBits());
      outputStream.write(data.getMessage());

      return outputStream.toByteArray();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }


}
