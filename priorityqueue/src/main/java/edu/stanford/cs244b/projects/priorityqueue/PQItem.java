package edu.stanford.cs244b.projects.priorityqueue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;


public class PQItem {

    private PQKey _key;
    private byte[] _message;

  public PQItem(Long priority, byte[] message) {
    this(priority, UUID.randomUUID(), message);
  }

  public PQItem(Long priority, UUID uuid, byte[] message) {
    _key = new PQKey(priority, uuid);
    _message = message;
  }

  public PQKey getKey() {
    return _key;
  }

  public byte[] getMessage() {
    return _message;
  }

  @Override
  public String toString() {
    return "PQItem{" + "_key=" + _key + ", _message=" + Arrays.toString(_message) + '}';
  }

  public static void write(PQItem pqItem, DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeLong(pqItem.getKey().getPriority());
    dataOutputStream.writeLong(pqItem.getKey().getUuid().getMostSignificantBits());
    dataOutputStream.writeLong(pqItem.getKey().getUuid().getLeastSignificantBits());
    dataOutputStream.writeLong(pqItem.getMessage().length);
    dataOutputStream.write(pqItem.getMessage());
  }

  public static PQItem read(DataInputStream dataOutputStream) throws IOException {
    long priority = dataOutputStream.readLong();
    long uuidMSB = dataOutputStream.readLong();
    long uuidLSB = dataOutputStream.readLong();
    long msgSize = dataOutputStream.readLong();
    byte[] message = new byte[(int)msgSize];
    dataOutputStream.readFully(message);
    return new PQItem(priority, new UUID(uuidMSB, uuidLSB), message);
  }
}