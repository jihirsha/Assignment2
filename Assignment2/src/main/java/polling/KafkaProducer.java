package polling;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

//import org.apache.log4j.*;
//import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaProducer{

    private static Producer<Integer, String> producer;
    private final Properties properties = new Properties();


     KafkaProducer() {

        properties.put("metadata.broker.list", "54.149.84.25:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        producer = new Producer(new ProducerConfig(properties));
    }

    public void sendMessage(String pollResult)
    {
        String topic = "cmpe273-topic";
        KeyedMessage<Integer,String> data = new KeyedMessage<Integer, String>(topic,pollResult);
        System.out.println(pollResult);
        producer.send(data);
        producer.close();
    }
}