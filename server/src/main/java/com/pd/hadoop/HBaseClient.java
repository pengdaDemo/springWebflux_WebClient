/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 *
 */

package com.pd.hadoop;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//@Slf4j
@Component
public class HBaseClient {

    public Connection connection;

//    public HBaseClient(Configuration configuration) throws IOException {
//        connection = ConnectionFactory.createConnection(configuration);
//    }

    public boolean isTableExist(String tableName){
        try {
            return connection.getAdmin().tableExists(TableName.valueOf(tableName));
        } catch (IOException e) {
            throw new RuntimeException("HBase 'isTableExist' wrong");
        }
    }

    /**
     * 创建表
     *
     * @param tableName  表名
     * @param columnFamily  列族名称
     * @return 结果
     */
    public boolean createTable(String tableName, List<String> columnFamily) {
        TableName name = TableName.valueOf(tableName);
        try (Admin admin = connection.getAdmin()){
            if (admin.tableExists(name)) {
                return false;
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            columnFamily.forEach(cf->{
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(cf);
                //是否需要多版本
                hColumnDescriptor.setMaxVersions(1);
                tableDescriptor.addFamily(hColumnDescriptor);
            });
            admin.createTable(tableDescriptor);
        } catch (IOException e) {
            throw new RuntimeException("HBase 'createTable' wrong");
        }
        return true;
    }


    /**
     * 插入数据或更新数据
     *
     * @param tableName 表名
     * @param rowKey 键值
     * @param columnFamily 列族名
     */
    public void putData(String tableName , String rowKey , String columnFamily, Map<byte[],byte[]> map){
        try (Table table = connection.getTable(TableName.valueOf(tableName))){
            Put put = new Put(Bytes.toBytes(rowKey));
            map.forEach((k,v)-> put.addColumn(Bytes.toBytes(columnFamily),k,v));
            table.put(put);
            map.clear();
            map = null;
        } catch (Exception e) {
            throw new RuntimeException("HBase 'putData' wrong  "+e.getMessage());
        }
    }

    /**
     * 插入多条数据或更新数据
     *
     * @param tableName 表名
     */
    public void putDatas(String tableName , List<Put> puts){
        try (Table table = connection.getTable(TableName.valueOf(tableName))){
            table.put(puts);
        } catch (Exception e) {
            throw new RuntimeException("HBase 'putDatas' wrong---"+e.getMessage());
        }
    }

    /**
     * 根据row删除数据
     *
     * @param tableName 表名
     * @param row 键值
     */
    public void deleteByRow(String tableName, String row){
        try (Table table = connection.getTable(TableName.valueOf(tableName))){
            Delete delete = new Delete(Bytes.toBytes(row));
            table.delete(delete);
        } catch (Exception e) {
            throw new RuntimeException("HBase 'deleteByRow' wrong---"+e.getMessage());
        }
    }

    /**
     * 删除列族
     *
     * @param tableName 表名
     * @param columnFamily 列族
     */
    public void deleteColumnFamily(String tableName , String columnFamily){
        try (Admin admin = connection.getAdmin()){
            TableName name = TableName.valueOf(tableName);
            admin.deleteColumn(name,Bytes.toBytes(columnFamily));
        } catch (IOException e) {
            throw new RuntimeException("HBase 'deleteColumnFamily' wrong");
        }
    }

    /**
     * 删除指定列
     *
     * @param tableName 表名
     * @param rowKey 键
     * @param columnFamily 列族
     * @param column 列
     */
    public void deleteColumnQualifier(String tableName, String rowKey, String columnFamily , String column){
        try (Table table = connection.getTable(TableName.valueOf(tableName))){
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addColumns(Bytes.toBytes(columnFamily),Bytes.toBytes(column));
            table.delete(delete);
        } catch (IOException e) {
            throw new RuntimeException("HBase 'deleteColumnQualifier' wrong");
        }
    }

    /**
     * 删除表
     *
     * @param tableName 表名
     */
    public void dropTable(String tableName){
        TableName name = TableName.valueOf(tableName);
        try (Admin admin = connection.getAdmin()){
            if (admin.tableExists(name)){
                admin.disableTable(name);
                admin.deleteTable(name);
            }
        } catch (IOException e) {
            throw new RuntimeException("HBase 'dropTable' wrong");
        }
    }

    /**
     * 读取一行数据
     *
     * @param tableName 表名
     * @param rowKey key
     * @return 结果集
     */
    public Result getRow(String tableName, String rowKey){
        try (Table table = connection.getTable(TableName.valueOf(tableName))){
            Get get = new Get(Bytes.toBytes(rowKey));
            return table.get(get);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean exists(String tableName, String rowKey){
        try (Table table = connection.getTable(TableName.valueOf(tableName))){
            Get get = new Get(Bytes.toBytes(rowKey));
            return table.exists(get);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 读取一批数据 闭区间
     *
     * @param tableName 表名
     * @param filterList filterList
     * @return ResultScanner
     */
    public ResultScanner getScanner(String tableName , String family, FilterList filterList){
        ResultScanner resultScanner;
        byte[] familyBytes = Bytes.toBytes(family);
        try (Table table = connection.getTable(TableName.valueOf(tableName))){
            Scan scan = new Scan();
            scan.setMaxVersions(1);
            scan.addColumn(familyBytes,Bytes.toBytes("id"));
            scan.addColumn(familyBytes,Bytes.toBytes("type"));
            scan.addColumn(familyBytes,Bytes.toBytes("name"));
            scan.addColumn(familyBytes,Bytes.toBytes("uri"));
            scan.addColumn(familyBytes,Bytes.toBytes("length"));
            scan.addColumn(familyBytes,Bytes.toBytes("clientId"));
            scan.addColumn(familyBytes,Bytes.toBytes("date"));
            scan.addColumn(familyBytes,Bytes.toBytes("bucket"));
            scan.addColumn(familyBytes,Bytes.toBytes("exist"));
            scan.addColumn(familyBytes,Bytes.toBytes("pdfSplit"));
            if (null!=filterList){
                scan.setFilter(filterList);
            }
            resultScanner = table.getScanner(scan);
            return resultScanner;
        } catch (IOException e) {
            throw new RuntimeException("HBase 'getScanner' wrong");
        }
    }

    /**
     * close
     * @param admin admin
     * @param scanner scanner
     * @param table table
     */
    private void close(Admin admin, ResultScanner scanner , Table table){
        if (admin != null){
            try {
                admin.close();
            } catch (IOException e) {
                throw new RuntimeException("HBase 'admin' close wrong");
            }
        }

        if (scanner != null){
            scanner.close();
        }

        if (table != null){
            try {
                table.close();
            } catch (IOException e) {
                throw new RuntimeException("HBase 'getScanner' close  wrong");
            }
        }
    }

    public void listTables() throws IOException {
        Admin admin = connection.getAdmin();
        for (HTableDescriptor hTableDescriptor : admin.listTables()) {
            System.out.println(hTableDescriptor.getNameAsString());
        }
        for (HColumnDescriptor files : admin.getTableDescriptor(TableName.valueOf("files")).getColumnFamilies()) {
            files.getValues().forEach((k,v) ->{
                System.out.println("************** value *******************");
                System.out.println(k);
                System.out.println(v);
            });
        }
        close(admin,null,null);
    }

}
