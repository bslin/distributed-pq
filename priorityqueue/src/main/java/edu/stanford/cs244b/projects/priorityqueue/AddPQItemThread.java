package edu.stanford.cs244b.projects.priorityqueue;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;


@Component
class AddPQItemThread implements DisposableBean, Runnable {
    private PriorityQueue pq = SingletonInstances.PRIORITY_QUEUE;
    private Consumer<String, PQItem> _consumer;

    @SuppressWarnings("FieldCanBeLocal")
    private Thread _thread;
    private volatile boolean _shutdown;

    AddPQItemThread(){
        _consumer = createConsumer();
        _thread = new Thread(this);
        _thread.start();
    }

    @Override
    public void run() {
        try {
            while (!_shutdown) {
                ConsumerRecords<String, PQItem> consumerRecords = _consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, PQItem> record : consumerRecords) {
                    System.out.println(String.format("Got Message! %s, %s, %s", record.key(), record.value(), record.offset()));
                    pq.add(record.value());
                    // Add one to indicate that we are done with the current offset (so on restart we go to the next offset)
                    SingletonInstances.ADD_PQ_ITEM_CONSUMER_OFFSET.set(record.offset() + 1);
                }

            }
        } catch (Exception ex){
            System.out.println("ERROR!!! Need To SHUTDOWN Service! " + ex);
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void destroy() throws InterruptedException {
        _shutdown = true;
        while (_thread.isAlive()) {
            System.out.println("Waiting for PQAddConsumer to shutdown");
            Thread.sleep(100);
        }
    }


    private static Consumer<String, PQItem> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.ADD_PQ_ITEM_BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PQItemDeserializer.class.getName());

        // Create the consumer using props.
        Consumer<String, PQItem> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic.
        String topic = SingletonInstances.getAddPQItemTopic();
        long offset = SingletonInstances.ADD_PQ_ITEM_CONSUMER_OFFSET.get();
        System.out.println(String.format("ADD PQ Item Topic: %s, offset: %s", topic, offset));
        TopicPartition topicPartition = new TopicPartition(topic, 0);
        consumer.assign(Collections.singleton(topicPartition));
        consumer.seek(topicPartition, offset);
        return consumer;
    }

}