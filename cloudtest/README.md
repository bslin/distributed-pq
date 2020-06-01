# VM Specs

# Commands
1. VM setup
    Run this for every VM that you want to host distributed priority queue nodes on
    ```bash
    ./vm_setup.sh
    ```
1. Python setup
    This is only needed for VMs that you want to run test clients on
    ```bash
    ./python_setup.sh
    ```

1. Start zookeeper
    Run this only on one of the VMs. Take node of its IP address (you will need it for the next command).
    ```bash
    ./start_zk.sh
    ```
1. Start distributed priority queue server
    Run this on every VM that you want to host a distributed priority queue node on.
    For our setup, ```$total_instance_num=5```, ```$instance_num``` is from 0 to 4 for each VM, and ```$zk_ip=172.31.15.144```
    CAUTION!!: This command causes local file changes, make sure you have the commandline variables right.
    ```bash
    ./start_server.sh $instance_num $total_instance_num $zk_ip
    ```
1. Run test
    1. If necessary, update the ```test.py``` script with the IPs of the VMs hosting the pq nodes.
    1. Activate python virtual environment
        ```bash
        cd ~/cs244b/final_project/
        source venv/bin/activate
        cd ~/cs244b/final_project/cloudtest/
        ```
    1. Check test script options with
        ```bash
        python3 test.py --help
        ```
    1. Run a sample test with 5 consecutive clients, talking to all 5 hosts, starting to pop after first adding 10000 elements with initial cleanup
        ```bash
        test.py --num_clients=5 --num_elements=10000 --num_hosts=5 --clean_up
        ```

1. Shutting down distributed pq system in the following order
    1. Shutdown node server with Ctrl+C
    1. Shutdown Kafka broker with
        ```bash
        ~/cs244b/kafka_2.12-2.5.0/bin/kafka-server-stop.sh
        ```
    1. After doing the above two steps for all VMs involved, shut down zookeeper
        ```bash
         ~/cs244b/kafka_2.12-2.5.0/bin/zookeeper-server-stop.sh
        ```