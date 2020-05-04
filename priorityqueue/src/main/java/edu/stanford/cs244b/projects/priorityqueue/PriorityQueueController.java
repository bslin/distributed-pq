package edu.stanford.cs244b.projects.priorityqueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PriorityQueueController {
	private PriorityQueue _pq = SingletonInstances.PRIORITY_QUEUE;
	private AddPQProducer _pqProducer = new AddPQProducer();

	PriorityQueueController() {
	}

	@RequestMapping(value = "/pop", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public PQItem pop() {
		return _pq.pop();
	}

	@RequestMapping(value = "/commit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public PQKey commit(
			@RequestParam(value = "priority") long priority,
			@RequestParam(value = "uuid") String uuid
	) {
		PQKey pqKey = new PQKey(priority, UUID.fromString(uuid));
		_pq.commit(pqKey);
		return pqKey;
	}

	@RequestMapping(value = "/abort", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public PQKey abort(
			@RequestParam(value = "priority") long priority,
			@RequestParam(value = "uuid") String uuid
	) {
		PQKey pqKey = new PQKey(priority, UUID.fromString(uuid));
		_pq.abort(pqKey);
		return pqKey;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public PQItem add(
			@RequestParam(value = "priority") long priority,
			@RequestParam(value = "message") byte[] message
	) {
		PQItem pqItem = new PQItem(priority, message);
		_pqProducer.produce(Collections.singletonList(pqItem));
		return pqItem;
	}

	// For debugging only
	@RequestMapping(value = "/dump", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<PQItem> dump(@RequestParam(value = "limit", defaultValue="10") long limit) {
		Collection<PQItem> ret = new ArrayList<>();
		Iterator<PQItem> itemIterator = _pq.iterator();
		while (ret.size() < limit && itemIterator.hasNext()) {
			ret.add(itemIterator.next());
		}
		return ret;
	}

}