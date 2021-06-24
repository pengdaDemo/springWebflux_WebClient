package com.pd.hadoop.hbase;/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 *
 */

import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.pd.hadoop.HBaseClient;
import model.CrawlBatch;
import model.CrawlBatchType;
import model.Task;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Future;

public class HBaseClientTest {
    private Logger logger = LoggerFactory.getLogger(HBaseClientTest.class);

    private static final String MD5_BAK = "md5_back";

    private static final String columnFamily = "d";


    public static void main(String[] args) throws Exception {
        MongoClient MongoClient = new MongoClient(new MongoClientURI("mongodb://admindb:jkVp838Vcc@172.16.70.57:30862/admin"));
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClient,"crawler");
        String topic = "md5_back";
        Properties props = new Properties();
        //kafka消费的的地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.20.100:6667,172.16.20.101:6667,172.16.20.102:6667");
        //序列化
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "groupId_6");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //是否自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        //从poll(拉)的回话处理时长
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 3000);
        //超时时间
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        //一次最大拉取的条数
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3000);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(props);
        //订阅主题列表topic
        consumer.subscribe(Arrays.asList(topic));
        System.out.println("---------开始消费---------");
        int count = 0;
        try {
            boolean running = true;
            while (running) {
                String batchId = Hashing.md5().hashString(DateFormatUtils.format(new Date(), "yyyy-MM-dd"), Charset.forName("utf8")).toString();
                CrawlBatch crawlBatch = new CrawlBatch(batchId, "6c02fd4427dd49ea908af023f99ec840",0, 0, 0, 86400, CrawlBatchType.UPDATE);
                ConsumerRecords<String, String> records = consumer.poll(300);
                for (ConsumerRecord<String, String> record : records) {
                    JSONObject jsonObject = JSONObject.parseObject(record.value());
                    Task task = new Task();
                    task.file = true;
                    task.url = jsonObject.getString("url");
                    task.originUrl = jsonObject.getString("pageUrl");
                    task.fileName = jsonObject.getString("name");
                    task.seedId = jsonObject.getString("seedId");
                    task.level=3;
                    task.order = 8000;
                    task.taskOrderLevel = 8000;
                    task.id = UUID.randomUUID().toString();
                    task.createTime = System.currentTimeMillis();
                    task.crawlBatchId = batchId;
                    task.filter = false;
                    mongoTemplate.insert(task,"_sys_task");
                    count++;
                    System.out.println("当前消费数量：" + count);
                    if(count%100000 == 0) {
                        try {
                            mongoTemplate.insert(crawlBatch, "_sys_crawl_batch");
                        } catch (Exception e) {
                        }
                        if(mongoTemplate.count(new Query(), "_sys_task") > 800000) {
                            Thread.sleep(60*60*1000);
                        }
                    }
                }
                // 异步提交offset
                consumer.commitAsync(new OffsetCommitCallback() {
                    @Override
                    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                        if (exception != null) {
                            throw new RuntimeException("提交offset失败");
                        }
                    }
                });
                if (Thread.currentThread().isInterrupted()) {
                    running = false;
                }
            }
        } finally {
            consumer.close();
            MongoClient.close();
        }
    }


    public void bianli() throws Exception {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.20.100:6667,172.16.20.101:6667,172.16.20.102:6667");//生产环境kafka地址，多个地址用逗号分割
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put("max.block.ms", "5000");
        p.put("max.request.size", "5242880");
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p);


        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "ambari01.fydata:2181,ambari02.fydata:2181,ambari03.fydata:2181");
        HBaseClient hbaseClient = new HBaseClient();
        hbaseClient.connection = ConnectionFactory.createConnection(configuration);


        try {
            ResultScanner scanner = hbaseClient.getScanner(MD5_BAK, columnFamily, null);
            Result[] results = scanner.next(300);
            int count = 0;
            for (Result result : results) {
                if(count > 13784299) {
                    HbaseFileInfo dto = HbaseFileInfo.BinaryToObject(result);
                    String rowKey = Bytes.toString(result.getRow());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("url", dto.getUrl());
                    jsonObject.put("pageUrl", dto.getPageUrl());
                    jsonObject.put("seedId", dto.getSeedId());
                    jsonObject.put("name", dto.getName());
                    if (dto.getUrl() != null && dto.getUrl().startsWith("http") && dto.isExist()) {
                        ProducerRecord<String, String> record = new ProducerRecord("md5_back", rowKey, jsonObject.toString());
                        Future<RecordMetadata> future = kafkaProducer.send(record);
                        RecordMetadata recordMetadata = future.get();
                        if (!recordMetadata.hasOffset()) {
                            logger.error("kafka写入失败, rowKey：" + rowKey);
                        }
                    }
                }
                count++;
            }

            while ((results = scanner.next(300)) != null) {
                for (Result result : results) {
                    if(count > 13784299) {
                        try {
                            HbaseFileInfo dto = HbaseFileInfo.BinaryToObject(result);
                            String rowKey = Bytes.toString(result.getRow());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("url", dto.getUrl());
                            jsonObject.put("pageUrl", dto.getPageUrl());
                            jsonObject.put("seedId", dto.getSeedId());
                            jsonObject.put("name", dto.getName());
                            if (dto.getUrl() != null && dto.getUrl().startsWith("http") && dto.isExist()) {
                                ProducerRecord<String, String> record = new ProducerRecord("md5_back", rowKey, jsonObject.toString());
                                Future<RecordMetadata> future = kafkaProducer.send(record);
                                RecordMetadata recordMetadata = future.get();
                                if (!recordMetadata.hasOffset()) {
                                    logger.error("kafka写入失败, rowKey：" + rowKey);
                                }
                            }
                        } catch (Exception e) {
                            logger.error("kafka写入失败, ", e);
                        }
                    }
                    count++;
                }
                logger.info("当前遍历数量: " + count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hbaseClient.connection.close();
        }
    }

    public void chuli(Result[] results, KafkaProducer<String, String> kafkaProducer) throws Exception {
        for (Result result : results) {
            HbaseFileInfo dto = HbaseFileInfo.BinaryToObject(result);
            String rowKey = Bytes.toString(result.getRow());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", dto.getUrl());
            jsonObject.put("pageUrl", dto.getPageUrl());
            jsonObject.put("seedId", dto.getSeedId());
            jsonObject.put("name", dto.getName());
            if (dto.getUrl() != null && dto.getUrl().startsWith("http") && dto.isExist()) {
                ProducerRecord<String, String> record = new ProducerRecord("md5_back", rowKey, jsonObject.toString());
                Future<RecordMetadata> future = kafkaProducer.send(record);
                RecordMetadata recordMetadata = future.get();
                if (!recordMetadata.hasOffset()) {
                    logger.error("kafka写入失败, rowKey：" + rowKey);
                }
            }
        }
    }


}