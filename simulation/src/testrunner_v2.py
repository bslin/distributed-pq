import random
import statistics
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

    def __init__(self, queue, initial_item_count, num_pops, num_concurrent_pops, debug=True):
        if (num_pops > initial_item_count):
            raise Exception("num_pops is greater than initial_item_count.")

        self.pq = queue # Priority Queue
        self.initial_item_count = initial_item_count
        self.num_pops = num_pops
        self.num_concurrent_pops = num_concurrent_pops
        self.debug = True

    def run(self):
        for i in range(self.initial_item_count):
            self.pq.add(random.random())
        out, err = [], 0
        result = {}
        ops = self.generate_traffic(self.num_pops, self.num_concurrent_pops)
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
                if not self.pq.is_queue_empty(best_q_idx):
                    value = self.pq.pop_from_q(best_q_idx)
                    value_idx = self.pq.position(value)
                    out.append(value_idx)
                else:
                    err += 1
                del result[op[0]]
            # Background
            self.pq.background()

        if self.debug:
            out_sorted = sorted(out)
            print(f'PQ: {self.pq}; Num_pop: {len(out)}; Concurrent: {self.num_concurrent_pops}; '
                  f'Average: {statistics.mean(out)}; Medium: {statistics.median(out)}; '
                  f'90th percentile: {self.get_percentile(out_sorted, 90)}; '
                  f'99th percentile: {self.get_percentile(out_sorted, 99)}; ErrorCount: {err}')
        return (out, err)

    # simulate more fine-grained operation on the distributed pq, but explicity making requests to
    # query the sub queues and interleaving those requests
    def generate_traffic(self, num_pop, num_concurrent):
        # each pop consists of self.pq.num_queues_lb_pop pq peek operation, followed by a pop
        # each add consists of 1 pq add operation
        ret = []
        ops = []
        for i in range(num_pop):
            ops.append([])
            for j in range(self.pq.num_queues_lb_pop):
                ops[i].append((i, RequestType.PEEK))
            ops[i].append((i, RequestType.POP))
        num_ops_per_pop = len(ops[0])
        for i in range(len(ops) * num_ops_per_pop):
            concurrent_batch = i // (num_ops_per_pop * num_concurrent)
            idx_in_batch = i % (num_ops_per_pop * num_concurrent)
            pop_idx = concurrent_batch * num_concurrent + idx_in_batch % num_concurrent
            op_idx =  idx_in_batch // num_concurrent
            if (pop_idx < num_pop) and (op_idx < len(ops[0])):
                ret.append(ops[pop_idx][op_idx])
        return ret

    # statistics.quantiles is only introduced in python 3.8
    def get_percentile(self, data, percentile):
        if not sorted(data):
            data = sorted(data)
        idx = len(data) * percentile // 100 - 1
        return data[idx]