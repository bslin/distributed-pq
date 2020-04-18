import random
import statistics


class TestRunner:

    def __init__(self, queue, consumer_lag, num_iterations):
        self.pq = queue # Priority Queue
        self.consumer_lag = consumer_lag
        self.num_iterations = num_iterations

    def run(self):
        for i in range(self.consumer_lag):
            self.pq.add(random.random())

        out = []
        for iteration in range(1, self.num_iterations):
            # Add
            self.pq.add(random.random())

            # Pop
            value = self.pq.pop()
            value_idx = self.pq.position(value)
            out.append(value_idx)

            # Background
            self.pq.background()

        print(f'PQ: {self.pq}; Average: {statistics.mean(out)}; Median: {statistics.median(out)}')
        return out
