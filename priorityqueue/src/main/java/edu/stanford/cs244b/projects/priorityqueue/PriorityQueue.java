package edu.stanford.cs244b.projects.priorityqueue;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Concurrent Priority Queue. Guarantees at least once semantics.
 */
public class PriorityQueue {

	private ListeningScheduledExecutorService _commitTimeoutExecutorService;
	private ConcurrentSkipListMap<PQKey, ScheduledFuture<PQItem>> _revertTasks;

	private ConcurrentSkipListMap<PQKey, PQItem> _pq;
	private ConcurrentSkipListMap<PQKey, PQItem> _committedItems;

	public PriorityQueue() {
		_commitTimeoutExecutorService = MoreExecutors.listeningDecorator(
				Executors.newScheduledThreadPool(Constants.PQ_COMMIT_TIMEOUT_THREAD_COUNT)
		);

		_pq = new ConcurrentSkipListMap<>();
		_committedItems = new ConcurrentSkipListMap<>();

		_revertTasks = new ConcurrentSkipListMap<>();
	}
	public void add(PQItem pqItem) {
		_committedItems.put(pqItem.getKey(), pqItem);
		_pq.put(pqItem.getKey(), pqItem);
	}

	public PQItem pop() {
		PQItem pqItem;
		try {
			pqItem = _pq.pollFirstEntry().getValue();
		} catch (NullPointerException ex) {
			return null;
		}

		@SuppressWarnings("UnstableApiUsage")
		ListenableScheduledFuture<PQItem> future = _commitTimeoutExecutorService.schedule(() -> {
			_pq.putIfAbsent(pqItem.getKey(), pqItem);
			System.out.println("Aborted! " + pqItem);
			return pqItem;
		}, Constants.PQ_COMMIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);

		_revertTasks.put(pqItem.getKey(), future);
		future.addListener(() -> _revertTasks.remove(pqItem.getKey(), future), _commitTimeoutExecutorService);

		return pqItem;
	}

	public PQItem peek() {
		return _pq.firstEntry().getValue();
	}

	public void commit(PQKey pqKey) {
		ScheduledFuture<PQItem> future = _revertTasks.remove(pqKey);

		if (future != null) {
			future.cancel(false);
		}
		_committedItems.remove(pqKey);
		_pq.remove(pqKey);
	}

	public void abort(PQKey pqKey) {
		ScheduledFuture<PQItem> future = _revertTasks.remove(pqKey);
		if (future != null) {
			future.cancel(false);
		}

		PQItem pqItem = _committedItems.get(pqKey);
		if (pqItem != null) { // We need to do this check in case this has been previously committed
			_pq.putIfAbsent(pqItem.getKey(), pqItem);
		}
	}

	Iterator<PQItem> iterator() {
		return _committedItems.values().iterator();
	}
}