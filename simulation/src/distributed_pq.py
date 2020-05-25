import uuid

from blist import sorteddict
import random


class BasePriorityQueue:
    def __init__(self, num_queues, num_queues_lb_add=1, num_queues_lb_pop=1):
        self.map = sorteddict()
        self.num_queues_lb_add = num_queues_lb_add
        self.num_queues_lb_pop = num_queues_lb_pop
        self.queues = []
        self.queue_sum = []
        for i in range(num_queues):
            self.queues.append(sorteddict())
            self.queue_sum.append(0)

    # Public ADD
    def add(self, k):
        key = (k, uuid.uuid4())

        best_q_idx = None
        best_q_position = None
        for i in range(self.num_queues_lb_add):
            q_idx = self.get_rand_q_idx()
            q_position = self.get_queue(q_idx).keys().bisect_left(key)

            if best_q_position is None or best_q_position > q_position:
                best_q_idx = q_idx
                best_q_position = q_position

        return self.add_to_queue_uuid(best_q_idx, key)

    # Public POP
    def pop(self):
        if len(self.map) <= 0:
            raise ValueError("No Value!")

        best_q_idx = None
        best_q = None
        best_key = None
        for i in range(self.num_queues_lb_pop):
            q_idx, q = self.get_nonempty_rand_queue()
            q_key = q.keys()[0]

            if best_key is None or best_key > q_key:
                best_q_idx = q_idx
                best_q = q
                best_key = q_key

        key, v = best_q.popitem()
        self.queue_sum[best_q_idx] -= key[0]
        del self.map[key]
        return key[0]

    def is_queue_empty(self, q_idx):
        return len(self.get_queue(q_idx)) == 0

    def pop_from_q(self, q_idx):
        q = self.get_queue(q_idx)
        key, v = q.popitem()
        self.queue_sum[q_idx] -= key[0]
        del self.map[key]
        return key[0]

    def peek_from_q(self, q_idx):
        q = self.get_queue(q_idx)
        key = q.keys()[0]
        return key[0]

    # Public BACKGROUND function (for gossip)
    def background(self):
        pass

    # Public (For testing) get the position of a key
    def position(self, k):
        return self.map.keys().bisect_left((k, uuid.uuid4()))

    def add_to_queue(self, q_idx, k):
        key = (k, uuid.uuid4())
        return self.add_to_queue_uuid(q_idx, key)

    def add_to_queue_uuid(self, q_idx, key):
        q = self.get_queue(q_idx)
        q[key] = True
        self.map[key] = True
        self.queue_sum[q_idx] += key[0]

    def __len__(self):
        return len(self.map)

    def __str__(self):
        return f'BasePriorityQueue(nqueues:{len(self.queues)}, lag: {len(self)}, add:{self.num_queues_lb_add}, pop:{self.num_queues_lb_pop})'

    def get_queue(self, q_idx):
        return self.queues[q_idx % len(self.queues)]

    def get_rand_q_idx(self):
        q_idx = random.randint(0, len(self.queues) - 1)
        return q_idx

    def get_nonempty_rand_queue(self):
        if len(self.map) <= 0:
            raise ValueError("No Value!")

        q_idx = self.get_rand_q_idx()
        q = self.get_queue(q_idx)
        while len(q) == 0:
            q_idx = (q_idx + 1) % len(self.queues)
            q = self.get_queue(q_idx)
        return q_idx, q
