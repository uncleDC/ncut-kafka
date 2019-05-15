import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import util.ComputerMonitorUtil;
import util.SystemInfoTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class MyKafkaProducer2 {
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

            while(true){
                double cpuUsage = (int)(SystemInfoTest.getCpuUsage()*100)*0.01;
                //double cpuUsage = ComputerMonitorUtil.getCpuUsage();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String time = df.format(new Date());// new Date()为获取当前系统时间
                producer.send(new ProducerRecord<String, String>(topic, time+","+String.valueOf(cpuUsage)));
                System.out.println("Sent:" +  ":time, cpuUsage:" + time+","+String.valueOf(cpuUsage));
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            producer.close();
        }
    }
}