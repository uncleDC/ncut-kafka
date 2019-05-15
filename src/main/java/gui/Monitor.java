package gui;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import oshi.software.os.OSFileStore;
import oshi.util.FormatUtil;
import util.SystemInfoTest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class Monitor {
    private JPanel panel1;
    private JTextField textField1;
    private JButton 启动Button;
    private JTextArea textArea1;
    private JTextField textField2;
    public static String ip = "10.5.81.243";
    public static String topic = "test2";
    public static boolean isOK = false;
    public static Properties props = new Properties();
    Producer<String, String> producer = null;
    public static List<String> list = new ArrayList<String>();
    CaptureInfoThread thread = null;
    public Monitor() {
        启动Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(producer != null){
                    producer.close();
                }
                ip = textField1.getText();
                topic = textField2.getText();
                props.setProperty("bootstrap.servers", ip+":9092");

                producer = new KafkaProducer<String, String>(props);
                list.add("KafkaProducer已建立...\n");
                if(thread != null){
                    thread.setStop(true); // 设置共享变量为true
                    thread.interrupt(); // 阻塞时退出阻塞状态
                }
                thread  =  new CaptureInfoThread();
                thread.start();

            }
        });
        props.put("bootstrap.servers", ip+":9092");//集群
        props.put("acks", "all"); //应答机制
        props.put("retries", 0);//重试次数
        props.put("batch.size", 16384);//批量大小
        props.put("linger.ms", 1); //提交延时
        props.put("buffer.memory", 33554432); //缓存
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); //KV的序列化类
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    }

    public static void main(String[] args) throws Exception {
        // 界面属性
        JFrame frame = new JFrame("Linux 内存信息采集传感器");
        frame.setPreferredSize(new Dimension(500,400));
        Monitor monitor = new Monitor();
        frame.setContentPane(monitor.panel1);
        frame.setLocationRelativeTo(null);//在屏幕中居中显示
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    class CaptureInfoThread extends Thread {
        volatile boolean stop = false;

        public boolean isStop() {
            return stop;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        public void run() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            list.add("开始发送内存信息...\n");
            list.add("时间戳                               内存使用率:\n");
            Random random = new Random();
            int reads = 3109756;
            int writes = 2226726;
            while (true) {
                if(stop == true)
                    break;
                double CPUuseRate = (int)(SystemInfoTest.getCpuUsage()*100)*0.01;
                double CPUtemp = random.nextDouble()*10+33.3;

                String diskUsed = "39.43%";
                String time = df.format(new Date());// new Date()为获取当前系统时间
                reads += (int)(random.nextDouble()*557);
                writes += (int)(random.nextDouble()*957);
//                System.out.println((int)random.nextDouble()*557+","+writes);
//
//                producer.send(new ProducerRecord<String, String>(topic, time+","+String.format("%.2f",CPUuseRate)+","+String.format("%.2f",CPUtemp)));
//                producer.send(new ProducerRecord<String, String>(topic, time+","+String.format("%.2f",CPUuseRate)));
                producer.send(new ProducerRecord<String, String>(topic, time+","+String.format("%.2f",CPUtemp)));
                System.out.println(time+","+String.format("%.2f",CPUtemp));
//                list.add(time+",     "+String.format("%.2f",CPUuseRate) + "%,               "+String.format("%.2f",CPUtemp)+"°C\n");
                list.add(time+",     "+String.format("%.2f",CPUtemp)+"% \n");
//                list.add(time+",     "+diskUsed + "           "+String.valueOf(reads)+ "        "+String.valueOf(writes)+"\n");
                String s = "";
                for (String l : list) {
                    s+=l;
                }
                textArea1.setText(s);
                if (list.size() > 20)
                    list.clear();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            while (!stop) {
                System.out.println(getName() + " is running");
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("week up from blcok...");
                    stop = true; // 在异常处理代码中修改共享变量的状态
                }
            }
            System.out.println(getName() + " is exiting...");
        }
    }
}
