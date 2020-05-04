Setting up Kafka: https://kafka.apache.org/quickstart
```
rm -rf /tmp/zookeeper
rm -rf /tmp/kafka-logs
bin/zookeeper-server-start.sh config/zookeeper.properties 
bin/kafka-server-start.sh config/server.properties 
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic add-pq-item-0
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic add-pq-item-1
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic add-pq-item-2

```

To produce messages:
```
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic add-pq-item-0
```


Run:
````
rm -rf ./target && ./mvnw package && java -jar target/priorityqueue-0.0.1.war 0 1
````

Note: (message is base64 encoded), to decode go to:
https://www.base64decode.org/

Add:
```
curl -w '\n'  -X POST http://localhost:8080/add --data 'priority=1&message=abc'
```

Pop:
```
curl -w '\n'  -X POST "http://localhost:8080/pop"
```

commit:
```
curl -w '\n'  -X POST "http://localhost:8080/commit" --data 'priority=1&uuid=d4a0d380-a594-4e85-b6b3-936e9ccc0730'
```

abort:
```
curl -w '\n'  -X "POST http://localhost:8080/abort" --data 'priority=1&uuid=d4a0d380-a594-4e85-b6b3-936e9ccc0730'
```

dump:
```
curl -w '\n'  -X GET "http://localhost:8080/dump/?limit=100"
```