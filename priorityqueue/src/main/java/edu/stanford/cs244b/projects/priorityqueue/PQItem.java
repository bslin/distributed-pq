package edu.stanford.cs244b.projects.priorityqueue;

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
}