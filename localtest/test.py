import multiprocessing
import statistics
import time
from client import PQClient
from blist import sorteddict

num_elements = 1000
num_clients = 128
pqclient = PQClient(num_instances_per_host=10, num_peeks=2, hosts=['192.168.0.9'])


print("CLEARING")
print(pqclient.clear())

expected_map = sorteddict()


print("ADDING")

def add_worker(proc_num):
    pqclient.add(element=proc_num)

for i in range(num_elements):
    expected_map[i] = i

pool = multiprocessing.Pool(processes = 100)
pool.map(add_worker, range(num_elements))


input('Added initial elements. Press any key to continue test...')


timestamp_counter = multiprocessing.Value('i', 0)

def pop_worker(proc_num):
    global timestamp_counter
    results = []
    num_none = 0
    while num_none < 1:
        res = pqclient.pop()
        if res is None:
            num_none += 1
        else:
            """
            with timestamp_counter.get_lock():
                timestamp_counter.value += 1
                timestamp = timestamp_counter.value
            """
            timestamp = time.time_ns()

            results.append((timestamp, proc_num, res))
            if len(results) % 100 == 0:
                print(proc_num, results[-1])
    return results


pool = multiprocessing.Pool(processes = num_clients)
results = []
start_time_ns = time.monotonic_ns()
raw_results = pool.map(pop_worker, range(num_clients))
end_time_ns = time.monotonic_ns()

for raw_result in raw_results:
    results.extend(raw_result)

results.sort(key=lambda r: r[0])
print("Done!", results)

indexes = []
for result in results:
    ts = result[0]
    pid = result[1]
    k = result[2]['key']['priority']
    pos = expected_map.keys().bisect_left(k)
    print(ts, pid, k, pos)
    indexes.append(pos)
    try:
        del expected_map[k]
    except KeyError:
        pass

elapsed = (end_time_ns - start_time_ns) / 1e9
rate = num_elements / elapsed
print(f'Average: {statistics.mean(indexes)}; Median: {statistics.median(indexes)}; Elapsed: {elapsed}s or {rate} el/s')




