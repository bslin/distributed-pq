package edu.stanford.cs244b.projects.priorityqueue;

/**
 * Constants
 * TODO: Move some of these to configs
 */
public class Constants {
  public static final String ADD_PQ_ITEM_TOPIC = "add-pq-item-%s";
  public static final String ADD_PQ_ITEM_BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";
  public static final String ADD_PQ_ITEM_CONSUMER_GROUP = "add-pq-item-consumer-group";

  public static final String SNAPSHOT_DIR = "/tmp/distributedpq/%s/";
  public static final long SNAPSHOT_INTERVAL_MS = 30_000;

  public static final long PQ_COMMIT_TIMEOUT_MS = 10_000;
  public static final int PQ_COMMIT_TIMEOUT_THREAD_COUNT = 4;

}
