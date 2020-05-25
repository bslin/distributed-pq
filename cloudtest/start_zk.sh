#!/bin/bash
rm -rf /tmp/zookeeper
cd ~/cs244b/kafka_2.12-2.5.0
bin/zookeeper-server-start.sh config/zookeeper.properties
