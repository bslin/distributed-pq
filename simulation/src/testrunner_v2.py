import random
import statistics
import uuid
from enum import Enum

class Request:
    def __init__(self, id, type):
        self.id = id
        self.type = type

class RequestType(Enum):
    PEEK = 0
    POP = 1
    ADD = 2

class TestRunnerV2:

    def __init__(self, queue, consumer_lag, num_iterations):
        self.pq = queue # Priority Queue
        self.consumer_lag = consumer_lag
        self.num_iterations = num_iterations

    def run(self):
        for i in range(self.consumer_lag):
            self.pq.add(random.random())

        out = []
        # Add enough to prevent queue from getting empty
        for iteration in range(1, self.num_iterations):
            self.pq.add(random.random())
        ops = self.generate_traffic(self.num_iterations, 1)
        print("DEBUG ops_length={}".format(len(ops)))
        result = {}
        # op = (id, type)
        for op in ops:
            if op[1] == RequestType.PEEK:
                q_idx, q = self.pq.get_nonempty_rand_queue()
                q_key = q.keys()[0]
                if op[0] in result:
                    result[op[0]].append((q_key, q_idx))
                else:
                    result[op[0]] = [(q_key, q_idx)]
            if op[1] == RequestType.POP:
                best = min(result[op[0]], key=lambda q : q[0]) #(q_key, q_idx) with min q_key
                best_q_idx = best[1]
                value = self.pq.pop_from_q(best_q_idx)
                value_idx = self.pq.position(value)
                out.append(value_idx)
                out.append(value_idx)
                del result[op[0]]
            # Background
            self.pq.background()

        print(f'PQ: {self.pq}; Average: {statistics.mean(out)}; Median: {statistics.median(out)}')
        return out

    # simulate more fine-grained operation on the distributed pq, but explicity making requests to
    # query the sub queues and interleaving those requests
    def generate_traffic(self, num_pop, num_concurrent):
        # each pop consists of self.pq.num_queues_lb_pop pq peek operation, followed by a pop
        # each add consists of 1 pq add operation
        ret = []
        for i in range(num_pop):
            for j in range(self.pq.num_queues_lb_pop):
                ret.append((i, RequestType.PEEK))
            ret.append((i, RequestType.POP))
        return ret