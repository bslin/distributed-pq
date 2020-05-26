import argparse
import multiprocessing
import statistics
import time
import sys
sys.path.append('../')
from localtest.client import PQClient

# sample run python3 test.py --num_clients=10 --num_elements=10000 --num_hosts=5 --clean_up
parser = argparse.ArgumentParser()
parser.add_argument("--num_elements", help="number of elements to insert into the pq", default=1000, type=int)
parser.add_argument("--num_clients", help="number of clients to simutaneously pop from the pq", default=1, type=int)
parser.add_argument("--num_hosts", help="number of active pq hosts", default=1, type=int)
parser.add_argument("--clean_up",help="whether to clean up the pq before starting the test", action="store_true")
args = parser.parse_args()

num_elements = args.num_elements
num_clients = args.num_clients
all_hosts = ['172.31.15.144', '172.31.11.65', '172.31.3.128', '172.31.2.221', '172.31.11.164']
hosts = all_hosts[0:args.num_hosts]
pqclient = PQClient(num_instances_per_host=1, num_peeks=2, hosts=hosts)

def add_worker(proc_num):
    pqclient.add(element=proc_num)

def pop_worker(proc_num):
    results = []
    num_none = 0
    while num_none < 1:
        start_time = time.perf_counter()
        res = pqclient.pop()
        end_time = time.perf_counter()
        if res is None:
            num_none += 1
        else:
            t = end_time - start_time
            results.append((proc_num, end_time - start_time, res))
            if (len(results) -1) % 100 == 0:
                print(f'worker:{proc_num} result:{res} time:{t}')
    return results

    # statistics.quantiles is only introduced in python 3.8
def get_percentile(data, percentile):
    data = sorted(data)
    idx = len(data) * percentile // 100 - 1
    return data[idx]

def main():
    input(f'Starting test with elements: {num_elements}, hosts:{len(hosts)}, pop clients:{num_clients}, clean up:{args.clean_up}. Press any key to continue test...')
    if args.clean_up:
        print("CLEARING...")
        print(pqclient.clear())
    print("ADDING...")
    pool = multiprocessing.Pool(processes=100)
    pool.map(add_worker, range(num_elements))
    pool = multiprocessing.Pool(processes = num_clients)
    start_time = time.perf_counter()
    raw_results = pool.map(pop_worker, range(num_clients))
    end_time = time.perf_counter()
    results = [item for sublist in raw_results for item in sublist]
    print(f'Popped {len(results)} elements' )
    latencies = [item[1] for item in results]
    print(f'num:{len(latencies)} total_time_elapsed:{end_time - start_time} qps:{len(latencies) / (end_time - start_time)} '
          f'mean:{statistics.mean(latencies)} medium:{statistics.mean(latencies)} 90%:{get_percentile(latencies, 90)} 99%:{get_percentile(latencies, 99)}')

if __name__ == "__main__":
    main()