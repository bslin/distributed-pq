#!/bin/bash
PORT=8080
NUM=10
for i in {0..9}
do
  echo "START $i"
  java -Dserver.port=$((PORT + i)) -jar target/priorityqueue-0.0.1.war $i $NUM &
done

# To kill: pkill -f priorityqueue
