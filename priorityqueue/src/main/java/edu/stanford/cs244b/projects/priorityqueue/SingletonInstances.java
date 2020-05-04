package edu.stanford.cs244b.projects.priorityqueue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Class containing singleton objects.
 * This is a HACK!! These should be singleton objects probably injected via Spring.
 */
public class SingletonInstances {
  public static final PriorityQueue PRIORITY_QUEUE = new PriorityQueue();

  // CMD Line Arguments
  public static final AtomicInteger INSTANCE_NUMBER = new AtomicInteger();
  public static final AtomicInteger NUM_INSTANCES = new AtomicInteger();
  public static String getAddPQItemTopic() {
    return String.format(Constants.ADD_PQ_ITEM_TOPIC, SingletonInstances.INSTANCE_NUMBER);
  }

  // Global Variables
  public static final AtomicLong ADD_PQ_ITEM_CONSUMER_OFFSET = new AtomicLong();

}
