# Run local test

To start 10 priority queue server nodes, go to the priorityqueue (java directory) and run:
```
../localtest/start.sh 10
```

To stop, run:
```
../localtest/stop.sh
```

To run the client (verify destination host is correct in test.py):
```
python test.py
```