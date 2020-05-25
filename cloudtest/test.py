import multiprocessing
import statistics
import time
from localtest.client import PQClient

num_elements = 1000
num_clients = 1
#hosts = ['34.214.48.182', '54.185.251.31', '44.230.15.116', '54.212.105.110', '44.224.147.122']
hosts = ['34.214.48.182']
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
            if len(results) % 100 == 0:
                print(f'worker:{proc} result:{res} time:{t}')
    return results

def main():
    print("CLEARING...")
    print(pqclient.clear())
    print("ADDING...")
    pool = multiprocessing.Pool(processes=100)
    pool.map(add_worker, range(num_elements))
    input(f'Added {num_elements} initial elements. Press any key to continue test...')
    pool = multiprocessing.Pool(processes = num_clients)
    raw_results = pool.map(pop_worker, range(num_clients))
    print(f'Popped {len(raw_results)} elements' )

if __name__ == "__main__":
    main()