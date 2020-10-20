/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 *
 */

package com.pd.config;

import com.pd.hadoop.HBaseClient;
import com.pd.hadoop.HDfsClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.shaded.com.google.protobuf.ServiceException;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@PropertySource({"classpath:hbase.properties"})
public class HadoopConfig {

    @Value("${hBase.keyValue.maxsize}")
    private String keyValue;
    @Value("${hbase.hconnection.threads.max}")
    private String hconnectionMax;
    @Value("${hbase.hconnection.threads.core}")
    private String hconnectionCore;

//    @Bean
//    public HBaseClient hbaseClient() throws IOException, ServiceException {
//        Configuration configuration = HBaseConfiguration.create();
//        configuration.set("hbase.client.keyvalue.maxsize",keyValue);
//        configuration.set("hbase.hconnection.threads.max",hconnectionMax);
//        configuration.set("hbase.hconnection.threads.core",hconnectionCore);
//        HBaseAdmin.checkHBaseAvailable(configuration);
//        HBaseClient client = new HBaseClient(configuration);
//        return client;
//    }


    @Bean
    public HDfsClient hDfsClient() throws IOException, InterruptedException {
        HdfsConfiguration hdfsConfiguration = new HdfsConfiguration();
        return new HDfsClient(hdfsConfiguration);
    }
}
