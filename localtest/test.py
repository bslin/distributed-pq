import multiprocessing
import statistics
import time
from client import PQClient
from blist import sorteddict

num_elements = 1000
num_clients = 5
pqclient = PQClient()


print("CLEARING")
print(pqclient.clear())

expected_map = sorteddict()

print("ADDING")
for i in range(num_elements):
    pqclient.add(element=i)
    expected_map[i] = i


input('Added initial elements. Press any key to continue test...')


timestamp_counter = multiprocessing.Value('i', 0)

def worker(proc_num):
    global timestamp_counter
    results = []
    num_none = 0
    while num_none < 1:
        res = pqclient.pop()
        if res is None:
            num_none += 1
        else:
            with timestamp_counter.get_lock():
                timestamp_counter.value += 1
                timestamp = timestamp_counter.value

            results.append((timestamp, proc_num, res))
            if len(results) % 100 == 0:
                print(proc_num, results[-1])
    return results


pool = multiprocessing.Pool(processes = num_clients)
results = []
start_time_ns = time.monotonic_ns()
raw_results = pool.map(worker, range(num_clients))
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
    print(ts, pid, k)
    pos = expected_map.keys().bisect_left(k)
    indexes.append(pos)
    try:
        del expected_map[k]
    except KeyError:
        pass

elapsed = (end_time_ns - start_time_ns) / 1e9
rate = num_elements / elapsed
print(f'Average: {statistics.mean(indexes)}; Median: {statistics.median(indexes)}; Elapsed: {elapsed}s or {rate} el/s')




