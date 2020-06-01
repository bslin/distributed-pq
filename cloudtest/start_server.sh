#!/bin/bash
instance_num=${1:-0}
total_instance_num=${2:-1}
zk_ip=${3:-172.31.15.144}
echo instance_num: $instance_num
echo total_instance_num: $total_instance_num
echo zk_ip: $zk_ip
rm -rf /tmp/kafka-logs
cd ~/cs244b/kafka_2.12-2.5.0
sed -i "s/broker.id=0/broker.id=$instance_num/g" config/server.properties
sed -i "s/zookeeper.connect=localhost:2181/zookeeper.connect=$zk_ip:2181/g" config/server.properties
bin/kafka-server-start.sh config/server.properties &
# creating topics
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic add-pq-item-$instance_num --replica-assignment $instance_num
# rebuild server
cd ~/cs244b/final_project/priorityqueue/
./mvnw package
# launch server instance
java -jar target/priorityqueue-0.0.1.war $instance_num $total_instance_num
