package edu.stanford.cs244b.projects.priorityqueue;

import java.util.Collection;
import java.util.Properties;
import java.util.Random;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;


class AddPQProducer {
    private final Random _random = new Random();
    private final Producer<String, PQItem> _producer;

    public AddPQProducer() {
        _producer = createProducer();
    }


    public void produce(Collection<PQItem> pqItems) {

        int instance = _random.nextInt(SingletonInstances.NUM_INSTANCES.get());
        String topic = String.format(Constants.ADD_PQ_ITEM_TOPIC, instance);

        pqItems.forEach((pqItem) -> _producer.send(new ProducerRecord<>(topic, pqItem)));
        _producer.flush();
    }

    private Producer<String, PQItem> createProducer() {

        Properties props = new Properties();

        //Assign localhost id
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.ADD_PQ_ITEM_BOOTSTRAP_SERVERS);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PQItemSerializer.class.getName());

        return new KafkaProducer<>(props);
    }
}