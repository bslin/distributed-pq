package edu.stanford.cs244b.projects.priorityqueue;

import java.util.Iterator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;


@Component
class SnapshotThread implements DisposableBean, Runnable {
    private PriorityQueue pq = SingletonInstances.PRIORITY_QUEUE;

    @SuppressWarnings("FieldCanBeLocal")
    private Thread _thread;
    private volatile boolean _shutdown;

    SnapshotThread(){
        _thread = new Thread(this);
        _thread.start();
    }

    @Override
    public void run() {
        try {
            long lastSnapshotTime = System.currentTimeMillis();
            while (!_shutdown) {
                Thread.sleep(Math.max(lastSnapshotTime + Constants.SNAPSHOT_INTERVAL_MS - System.currentTimeMillis(), 0));
                long kafkaOffset = SingletonInstances.ADD_PQ_ITEM_CONSUMER_OFFSET.get();
                Iterator<PQItem> pqItemIterator = pq.iterator();

                lastSnapshotTime = System.currentTimeMillis();
                System.out.println(String.format("Running Snapshot! offset: %s, time: %s ", kafkaOffset, lastSnapshotTime));
                String lastSnapshot = SnapshotUtils.snapshot(kafkaOffset, pqItemIterator);
                System.out.println("Most recent snapshot: " + lastSnapshot);
            }
        } catch (Exception ex){
            System.out.println("ERROR!!! Need To SHUTDOWN Service! " + ex);
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void destroy() {
        _shutdown = true;
        _thread.interrupt();
    }
}