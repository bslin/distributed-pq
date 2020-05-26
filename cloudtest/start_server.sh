#!/bin/bash
echo instance_num: $1
echo total_instance_num $2
rm -rf /tmp/kafka-logs
cd ~/cs244b/kafka_2.12-2.5.0
sed -i "s/broker.id=0/broker.id=$1/g" config/server.properties
sed -i 's/zookeeper.connect=localhost:2181/zookeeper.connect=172.31.15.144:2181/g' config/server.properties
bin/kafka-server-start.sh config/server.properties &
# creating topics
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic add-pq-item-$1 --replica-assignment $1
# rebuild server
cd ~/cs244b/final_project/priorityqueue/
./mvnw package
# launch server instance
java -jar target/priorityqueue-0.0.1.war $1 $2
