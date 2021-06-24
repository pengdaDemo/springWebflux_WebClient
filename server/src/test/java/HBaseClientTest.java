/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 *
 */

import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.pd.ServerApplication;
import com.pd.hadoop.HBaseClient;
import com.pd.hadoop.HDfsClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class)
public class HBaseClientTest {
    private Logger logger = LoggerFactory.getLogger(HBaseClientTest.class);

    @Autowired
    protected WebApplicationContext context;

    private final String TABLE_NAME = "files";

    private final String MD5_TABLE_NAME = "md5";

    private final String MD5_BAK = "md5_back";

    private final String columnFamily = "d";

    private static HBaseClient hbaseClient = new HBaseClient();;

    @Autowired
    private HDfsClient hDfsClient;

    private MockMvc mockMvc;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static KafkaProducer<String, String> kafkaProducer = null;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    static {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.20.100:6667,172.16.20.101:6667,172.16.20.102:6667");//生产环境kafka地址，多个地址用逗号分割
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put("max.block.ms", "5000");
        p.put("max.request.size", "5242880");
        kafkaProducer = new KafkaProducer<>(p);
    }

    @Test
    public void isTableExist(){
        System.out.println(hbaseClient.isTableExist(TABLE_NAME));
    }

        private static Configuration configuration() {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://prod");
        conf.set("dfs.nameservices", "prod");
        conf.set("dfs.ha.namenodes.prod", "nn1,nn2");
        conf.set("dfs.namenode.rpc-address.prod.nn1", "ambari02.fydata:8020");
        conf.set("dfs.namenode.rpc-address.prod.nn2", "ambari03.fydata:8020");
        conf.set("dfs.client.failover.proxy.provider.prod", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
        conf.setBoolean("fs.hdfs.impl.disable.cache", true);
        conf.set("dfs.datanode.max.transfer.threads", "16384");
        return conf;
    }


    public static FileSystem getFileSystem() throws Exception{
        return FileSystem.newInstance(new URI("hdfs://prod"), configuration(), "hdfs");
    }

//    @Test
//    public void createTable(){
//        List<String> list = new ArrayList<>();
//        list.add(columnFamily);
//        System.out.println(hbaseClient.createTable(MD5_TABLE_NAME,list));
//        System.out.println(hbaseClient.createTable(TABLE_NAME,list));
//    }
//
//        @Test
//    public void dropTable(){
//        hbaseClient.dropTable(MD5_TABLE_NAME);
//        hbaseClient.dropTable(TABLE_NAME);
//    }


    @Test
    public void scanData() throws IOException {
        ResultScanner result = hbaseClient.getScanner(TABLE_NAME, "d", null);

        while (result.next()!=null)
        {
                Cell[] cells = result.next().rawCells();
                for (Cell cell : cells) {
                    System.out.println("RowName:" + new String(CellUtil.cloneRow(cell)) + " ");
                }
        }
        result.close();
    }

    @Test
    public void scanData2() throws IOException {
        ResultScanner result = hbaseClient.getScanner(TABLE_NAME, "d", null);
        int i = 0;
        while (result.next()!=null)
        {
            Result data = result.next();
            if (null != data){
                HbaseDTO hbaseDTO = HbaseDTO.BinaryToObject(data);
                System.out.println("id--------" + hbaseDTO.getId());
                System.out.println("Date------" +hbaseDTO.getDate());
                System.out.println("Bucket----" +hbaseDTO.getBucket());
                System.out.println("ClientId--" +hbaseDTO.getClientId());

                System.out.println(i);
                i++;
            }
        }
        result.close();
    }


    @Test
    public void getData(){
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "dev03:2181,dev02:2181,dev01:2181");
        try{
            hbaseClient.connection = ConnectionFactory.createConnection(configuration);
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://crawl:xRt3UsQCi5@172.19.79.10:27701,172.19.79.11:27701,172.19.79.9:27701/admin"));
            MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "scrolls");
            MongoCollection<Document> collection = mongoTemplate.getCollection("fs.files");
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Object id = document.get("_id");
                Document metadata = (Document)document.get("metadata");
                String contentType = metadata.get("contentType") == null?null:metadata.getString("contentType");
                String filename = document.get("filename") == null?null:document.getString("filename");
                if(id instanceof ObjectId) {
                    ObjectId objectId = (ObjectId) id;
                    try(InputStream inputStream = GridFSBuckets.create(mongoTemplate.getDb()).openDownloadStream(objectId);
                        ByteArrayOutputStream bo = new ByteArrayOutputStream();) {
//                        byte[] bytes = new byte[5*1024];
//                        int b = 0;
//                        while ((b=inputStream.read(bytes))!=-1) {
//                            bo.write(bytes, 0, b);
//                        }
//                        byte[] fileByte = bo.toByteArray();
//                        String md5 = DigestUtils.md5Hex(fileByte);
//                        HbaseFileInfo hbaseFileInfo = new HbaseFileInfo();
//                        hbaseFileInfo.setId(id.toString());
//                        hbaseFileInfo.setType(contentType);
//                        hbaseFileInfo.setName(filename);
//                        hbaseFileInfo.setMd5_id(md5);
//                        hbaseFileInfo.setExist(true);
//                        hbaseFileInfo.setLength(fileByte.length);
//                        HbaseDTO hbaseDTO = new HbaseDTO();
//                        hbaseDTO.setClientId("scrolls");
//                        hbaseDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
//
//                        if(fileByte.length>10*1024*1024) {
//                            String destPath = "/scrolls/scrolls/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + "/" + md5;
//                            hDfsClient.upload(new ByteArrayInputStream(fileByte), destPath);
//                            hbaseFileInfo.setExist(false);
//                            hbaseFileInfo.setUri(destPath);
//                            hbaseDTO.setUri(destPath);
//                        } else {
//                            hbaseDTO.setBinaryData(fileByte);
//                        }
//                        hbaseClient.putData(MD5_TABLE_NAME, id.toString(), "d", HbaseFileInfo.ObjectToBinary(hbaseFileInfo));
//
//                        hbaseClient.putData(TABLE_NAME, md5, "d", HbaseDTO.ObjectToBinary(hbaseDTO));
                        String rowKey = id.toString();
                        Result result = hbaseClient.getRow(MD5_TABLE_NAME, DigestUtils.md5Hex("https://static.ws.126.net/cnews/css13/img/end_money.png"));
                        HbaseFileInfo dto = HbaseFileInfo.BinaryToObject(result);
                        System.out.println(Bytes.toString(result.getRow()));
                        System.out.println(dto.getId());
                        System.out.println(dto.getName());
                        System.out.println(dto.getUrl());
                        System.out.println(dto.getUri());
                        System.out.println(dto.getPageUrl());
                        System.out.println(dto.getSeedId());
                        System.out.println(dto.getType());
                        System.out.println(dto.getLength());
                        System.out.println(result.current().getTimestamp());
                    }

                }

                break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*@Test
    public void deleteData(){
        hbaseClient.deleteByRow(tableName,"1003");
    }*/

    @Test
    public void scanMongo() {
        MongoCursor<Document> cursor = mongoTemplate.getCollection("fs.files").find().batchSize(500).iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Long length = document.getLong("length");
            if (length!=null&&length>10*1024*1024) {
                System.out.println(document.get("_id").toString());
            }
        }
    }


    @Test
    public void getHbaseFile() throws Exception{
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "ambari01.fydata:2181,ambari02.fydata:2181,ambari03.fydata:2181");
        String rowKey = DigestUtils.md5Hex("http://static.cninfo.com.cn/finalpage/2020-10-22/1208599796.PDF");
        try(Connection connection = ConnectionFactory.createConnection(configuration)) {
            Result result = getRow("md5", rowKey, connection);
            Cell cell = result.getColumnLatestCell("d".getBytes(), "md5_id".getBytes());
            if(cell !=null) {
                String fileMd5 = new String(CellUtil.cloneValue(cell));
                Result resultFile = getRow("files", fileMd5, connection);
                Cell fileCell = resultFile.getColumnLatestCell("d".getBytes(), "binaryData".getBytes());
                if(fileCell != null) {
                    byte[] data = CellUtil.cloneValue(fileCell);
                    System.out.println(data.length);
                }
                System.out.println(fileMd5);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public Result getRow(String tableName, String rowKey, Connection connection) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))){
            Get get = new Get(Bytes.toBytes(rowKey));
            return table.get(get);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    public void testHbaseData() throws Exception{
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "ambari01.fydata:2181,ambari02.fydata:2181,ambari03.fydata:2181");
        hbaseClient.connection = ConnectionFactory.createConnection(configuration);
        String rowKey = "000013865a095121dda1360163cdd7bc";
        //System.out.println(hbaseClient.isTableExist("oldFiles"));
        Result result = hbaseClient.getRow(MD5_TABLE_NAME, rowKey);
        HbaseFileInfo dto = HbaseFileInfo.BinaryToObject(result);
        System.out.println(Bytes.toString(result.getRow()));
        System.out.println(dto.getId());
        System.out.println(dto.getName());
        System.out.println(dto.getMd5_id());
        System.out.println(dto.getUrl());
        System.out.println(dto.getUri());
        System.out.println(dto.getPageUrl());
        System.out.println(dto.getSeedId());
        System.out.println(dto.getType());
        System.out.println(dto.getLength());
        System.out.println(dto.isExist());
        System.out.println(result.listCells().get(0).getTimestamp());

        List<Cell> cells = result.listCells();
        for (int i = 0; i < cells.size(); i++) {
            System.out.println(DateFormatUtils.format(new Date(cells.get(i).getTimestamp()),"yyyy-MM-dd HH:mm:ss"));
            System.out.println(new String(cells.get(i).getQualifier())+": "+new String(cells.get(i).getValue()));
        }
        System.out.println(result.current().getTimestamp());
    }
    @Test
    public void bianli() throws Exception {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "ambari01.fydata:2181,ambari02.fydata:2181,ambari03.fydata:2181");
        hbaseClient.connection = ConnectionFactory.createConnection(configuration);
        ResultScanner scanner = hbaseClient.getScanner(MD5_BAK, columnFamily, null);
        Result[] results = scanner.next(300);
        int count = 300;
        chuli(results);

        while ((results=scanner.next(300)) !=null){
            chuli(results);
            count+=300;
            System.out.println("当前遍历数量: " + count);
        }
    }
    public void chuli(Result[] results) throws Exception {
        for (Result result : results) {
            HbaseFileInfo dto = HbaseFileInfo.BinaryToObject(result);
            String rowKey = Bytes.toString(result.getRow());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", dto.getUrl());
            jsonObject.put("pageUrl", dto.getPageUrl());
            jsonObject.put("seedId", dto.getSeedId());
            jsonObject.put("name", dto.getName());
            if(dto.getUrl()!=null&&dto.getUrl().startsWith("http") && dto.isExist()){
                ProducerRecord<String, String> record = new ProducerRecord("md5_back", rowKey, jsonObject.toString());
                Future<RecordMetadata> future = kafkaProducer.send(record);
                RecordMetadata recordMetadata = future.get();
                if(!recordMetadata.hasOffset()) {
                    System.out.println("kafka写入失败, rowKey：" + rowKey);
                }
            }
//            System.out.println("rowKey:  "+Bytes.toString(result.getRow()));
//            System.out.println("id:  "+dto.getId());
//            System.out.println("name:  "+dto.getName());
//            System.out.println(dto.getMd5_id());
//            System.out.println("url:  "+dto.getUrl());
//            System.out.println("uri:  "+dto.getUri());
//            System.out.println("pageUrl:  "+dto.getPageUrl());
//            System.out.println("seedId:  "+dto.getSeedId());
//            System.out.println(dto.getType());
//            System.out.println(dto.getLength());
//            System.out.println("isExist:  "+ dto.isExist());
        }
    }

    @Test
    public void qianyi() throws Exception {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "ambari01.fydata:2181,ambari02.fydata:2181,ambari03.fydata:2181");
        configuration.set("hbase.client.keyvalue.maxsize", "10485760");
        hbaseClient.connection = ConnectionFactory.createConnection(configuration);
        hDfsClient.fs = HBaseClientTest.getFileSystem();
        MongoCursor<Document> cursor = mongoTemplate.getCollection("fs.files").find().noCursorTimeout(true).batchSize(300).iterator();
        int count =0;
        String id = "";
        boolean isError = false;
        try {
            while (cursor.hasNext()) {
                GridFSDownloadStream inputStream = null;
                ByteArrayOutputStream outputStream = null;
                try {
                    count++;//3940000
                    Document document = cursor.next();
                    id = document.get("_id").toString();
                    if(count < 780000 || hbaseClient.exists(MD5_TABLE_NAME, id)) {
                        continue;
                    };
                    //System.out.println(document.get("_id").toString());
                    String md5 = document.getString("md5");
                    if(md5 != null) {
                        //queue.offer(document);
                        //long size = queue.size();
                        logger.info("遍历数：" + count + "    ,源信息id：" + id+ "  ,队列总数：");
                        hbaseQianyi(document);
//                    if(size > 800) {
//                        start++;
//                        if(start > 100) {
//                            Thread.sleep(6*60*1000);
//                            start = 0;
//                        }
//                    } else {
//                        start = 0;
//                    }
                    }
                    document = null;
                } catch (Exception e) {
                    logger.error("文件id：" + id + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
        } finally {
            cursor.close();
            if(isError) {
                EmailSeed.seedMail("文件迁移预警", "1295831067@qq.com", "迁移程序断了！");
                try {
                    Thread.sleep(30 * 60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void hbaseQianyi(Document document) {
        String id = document.get("_id").toString();
        byte[] data = null;
        try(GridFSDownloadStream inputStream = GridFSBuckets.create(mongoTemplate.getDb()).openDownloadStream(new ObjectId(id));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            int i = 0;
            byte[]b=new byte[5*1024];
            while ((i=inputStream.read(b))!=-1){
                outputStream.write(b,0,i);
            }
            data = outputStream.toByteArray();
            String hexId = DigestUtils.md5Hex(data);
            long length = data.length;
            if(length != 0) {
                HbaseDTO hbaseDTO = new HbaseDTO();
                hbaseDTO.setDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                hbaseDTO.setClientId("mongo");
                hbaseDTO.setId(hexId);
                HbaseFileInfo hbaseFileInfo = new HbaseFileInfo();
                hbaseFileInfo.setLength(length);
                hbaseFileInfo.setMd5_id(hexId);
                hbaseFileInfo.setId(id);
                String type = document.get("metadata", Document.class).getString("contentType");
                if(type != null) {
                    hbaseFileInfo.setType(type);
                }
                if (length < 10 * 1024 * 1024) {
                    hbaseDTO.setBinaryData(data);
                    hbaseClient.putData(TABLE_NAME, hexId, columnFamily, HbaseDTO.ObjectToBinary(hbaseDTO));
                    hbaseFileInfo.setExist(true);
                    hbaseClient.putData(MD5_TABLE_NAME, id, columnFamily, HbaseFileInfo.ObjectToBinary(hbaseFileInfo));
                } else {
                    String destPath = "/scrolls/mongo/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + "/" + hexId;
                    hbaseClient.putData(TABLE_NAME, hexId, columnFamily, HbaseDTO.ObjectToBinary(hbaseDTO));
                    hbaseFileInfo.setExist(false);
                    hbaseFileInfo.setUri(destPath);
                    hbaseClient.putData(MD5_TABLE_NAME, id, columnFamily, HbaseFileInfo.ObjectToBinary(hbaseFileInfo));
                    hDfsClient.upload(new ByteArrayInputStream(data), destPath);
                }

            }
        } catch (Exception e) {
            logger.error("文件id：" + id + e.getMessage(), e);
        } finally {
            data = null;
        }


    }

    @Test
    public void download() throws Exception{
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "dev01:2181,dev02:2181,dev03:2181");
        hbaseClient.connection = ConnectionFactory.createConnection(configuration);
        String rowKey = "06dcafa526c8371701eb9caa517c228e";
        Result result = hbaseClient.getRow(TABLE_NAME, rowKey);
        HbaseDTO hbaseDTO = HbaseDTO.BinaryToObject(result);
        System.out.println(new String(hbaseDTO.getBinaryData()));
        InputStream inputStream = new ByteArrayInputStream(hbaseDTO.getBinaryData());
                OutputStream outputStream = new FileOutputStream("/home/pd/Documents/hbase下载");
        int i = 0;
        byte[]b=new byte[2024];
        while ((i=inputStream.read(b))!=-1){
            outputStream.write(b,0,i);
        }
        outputStream.close();
    }

    @Test
    public void foreachHbaseData() throws Exception{
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "dev01:2181,dev02:2181,dev03:2181");
        hbaseClient.connection = ConnectionFactory.createConnection(configuration);
        System.out.println(hbaseClient.isTableExist("oldFiles"));
        Table table = hbaseClient.connection.getTable(TableName.valueOf(TABLE_NAME));
        System.out.println(table.getRpcTimeout());
        System.out.println(table.getOperationTimeout());
        ResultScanner scan = table.getScanner(new Scan());
        int num=0;
        for (Result rst : scan) {
            num++;
            String rowKey = Bytes.toString(rst.getRow());
            // family    qualifiers     values
            NavigableMap<byte[], NavigableMap<byte[], byte[]>> familyMap = rst.getNoVersionMap();

            for (byte[] fByte : familyMap.keySet()) {

                NavigableMap<byte[], byte[]> quaMap = familyMap.get(fByte);

                String familyName = Bytes.toString(fByte);

                for (byte[] quaByte : quaMap.keySet()) {

                    byte[] valueByte = quaMap.get(quaByte);

                    String quaName = Bytes.toString(quaByte);


                    String value = Bytes.toString(valueByte);

                    String result = String.format("rowKey : %s | family : %s | qualifiers : %s | value : %s", rowKey, familyName, quaName, value);

                    System.out.println(result);
                }
            }
        }
        System.out.println(num);
            }

    /**
     * 查看文件下载情况
     * @throws Exception
     */
    @Test
    public void jiancewenjian() throws Exception {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://crawler:qkVp838VaA@172.16.40.1:28001,172.16.40.2:28001,172.16.40.3:28001/crawler"));
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "crawler");
        Query query = new Query();
        query.addCriteria(Criteria.where("parseErrorCountItemId").is(""));
        query.addCriteria(Criteria.where("file").is(true));
        query.fields().include("url");
        query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        query.limit(5000);
        List<Document> list = mongoTemplate.find(query, Document.class, "_sys_task_finished");
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "ambari01.fydata:2181,ambari02.fydata:2181,ambari03.fydata:2181");
        hbaseClient.connection = ConnectionFactory.createConnection(configuration);
        for (Document document:
             list) {
            String rowKey = DigestUtils.md5Hex(document.getString("url"));
            Result result = hbaseClient.getRow(MD5_TABLE_NAME, rowKey);
            HbaseFileInfo dto = HbaseFileInfo.BinaryToObject(result);
            if(dto.getUrl()!=null) {
                System.out.println(dto.getUrl());
            }
        }
    }

    @Test
    public void testAddClient() throws Exception {
        //mockMvc.perform(MockMvcRequestBuilders.post("/newScroll/bucket").header("clientId","TongQiLong").content(BucketType.read.name()));
    }

    @Test
    public void testAddBucket() throws Exception {
        //mockMvc.perform(MockMvcRequestBuilders.post("/newScroll/addBucket").header("clientId","admin").content(BucketType.write.name()));
    }

    @Test
    public void testImageMark() throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("functionType","mark");
        jsonObject.addProperty("color","#FFC0CB");
        jsonObject.addProperty("content","福韵数据");
        MockMultipartFile jsonFile = new MockMultipartFile("params", "", "", jsonObject.toString().getBytes());
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/pictureProcess").
            file(new MockMultipartFile("file", "test.jpg", null, new FileInputStream(new File("/home/cx/Downloads/sdss.png"))))
                                                                           .param("params",jsonObject.toString()));
        byte[] bytes = resultActions.andReturn().getResponse().getContentAsByteArray();
        FileOutputStream fileOutputStream = new FileOutputStream("/home/cx/Downloads/test1998.jpeg");
        fileOutputStream.write(bytes);
    }

    @Test
    public void testImageTailor() throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("functionType","tailor");
        jsonObject.addProperty("x",120);
        jsonObject.addProperty("y",120);
        jsonObject.addProperty("width",120);
        jsonObject.addProperty("height",120);
        MockMultipartFile jsonFile = new MockMultipartFile("params", "", "", jsonObject.toString().getBytes());
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/pictureProcess").
            file(new MockMultipartFile("file", "test.jpg", null, new FileInputStream(new File("/home/cx/Downloads/sdss.png"))))
                                                                            .param("params",jsonObject.toString()));
        byte[] bytes = resultActions.andReturn().getResponse().getContentAsByteArray();
        FileOutputStream fileOutputStream = new FileOutputStream("/home/cx/Downloads/test1999.jpeg");
        fileOutputStream.write(bytes);
    }

    @Test
    public void testImageCompress() throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("functionType","compress");
        jsonObject.addProperty("rate", 0.5);
        MockMultipartFile jsonFile = new MockMultipartFile("params", "", "", jsonObject.toString().getBytes());
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/pictureProcess").
            file(new MockMultipartFile("file", "test.jpg", null, new FileInputStream(new File("/home/cx/Downloads/sdss.png"))))
                                                                            .param("params",jsonObject.toString()));
        byte[] bytes = resultActions.andReturn().getResponse().getContentAsByteArray();
        FileOutputStream fileOutputStream = new FileOutputStream("/home/cx/Downloads/test1997.jpeg");
        fileOutputStream.write(bytes);
    }

    @Test
    public void testImageConvert() throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("functionType","convert");
        jsonObject.addProperty("afterType", "png");
        MockMultipartFile jsonFile = new MockMultipartFile("params", "", "", jsonObject.toString().getBytes());
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/pictureProcess").
            file(new MockMultipartFile("file", "test.jpg", null, new FileInputStream(new File("/home/cx/Downloads/sdss.png"))))
                                                                            .param("params",jsonObject.toString()));
        byte[] bytes = resultActions.andReturn().getResponse().getContentAsByteArray();
        FileOutputStream fileOutputStream = new FileOutputStream("/home/cx/Downloads/test1997.jpeg");
        fileOutputStream.write(bytes);
    }

    @Test
    public void listTable() throws IOException {
        hbaseClient.listTables();
    }



    @Test
    public void scanDataMD5() {
        ResultScanner result = hbaseClient.getScanner(MD5_TABLE_NAME,"d", null);
        result.forEach(data->{
            if (null != data){
                System.out.println("ID:"+Bytes.toString(data.getRow())+
                                       "---------"+Bytes.toString(data.getValue("d".getBytes(),"id".getBytes())));
            }
        });
        result.close();
    }
}