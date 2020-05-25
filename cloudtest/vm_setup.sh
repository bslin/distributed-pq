#!/bin/bash
sudo apt-get update
sudo apt-get install -y emacs
sudo apt-get install -y openjdk-11-jdk
echo JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/ >> ~/.bashrc
mkdir -p ~/cs244b/final_project
cd ~/cs244b/final_project/
git init .
git remote add origin https://github.com/bslin/distributed-pq.git
git pull origin master
cd ~/cs244b/final_project/priorityqueue/
./mvnw package
cd ~/cs244b
curl -o kafka_2.12-2.5.0.tgz http://mirrors.ibiblio.org/apache/kafka/2.5.0/kafka_2.12-2.5.0.tgz
tar -xzf kafka_2.12-2.5.0.tgz
rm -rf kafka_2.12-2.5.0.tgz
