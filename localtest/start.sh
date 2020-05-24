#!/bin/bash
PORT=8080
NUM=10
NUM_1=$((NUM -1))
for i in $(seq 0 $NUM_1)
do
  echo "START $i"
  java -Dserver.port=$((PORT + i)) -jar target/priorityqueue-0.0.1.war $i $NUM &
done

# To kill: pkill -f priorityqueue
