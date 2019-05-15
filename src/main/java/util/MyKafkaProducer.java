package util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyKafkaProducer {
    public static final String broker_list = "localhost:9092";
    public static final String topic = "test";  //kafka topic 需要和 flink 程序用同一个 topic
    public static void main(String[] args){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", broker_list);
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = null;
        try {
            producer = new KafkaProducer<String, String>(properties);
            for (int i = 0; i < 200; i++) {
                String msg = "Message " + i;
                double cpuUsage = ComputerMonitorUtil.getCpuUsage();
                //当前系统的内存使用率
                double memUsage = ComputerMonitorUtil .getMemUsage();
                //当前系统的硬盘使用率
                double diskUsage = ComputerMonitorUtil .getDiskUsage();
                Map<String, Double> ingredients = new HashMap<String, Double>();
                ingredients.put("cpuUsage", cpuUsage);
                ingredients.put("memUsage", memUsage);
                ingredients.put("diskUsage", diskUsage);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("computer", ingredients);
                System.out.println(jsonObj);

                producer.send(new ProducerRecord<String, String>(topic, jsonObj.toString()));
                System.out.println("Sent:" + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            producer.close();
        }

    }
}