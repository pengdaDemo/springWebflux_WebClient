/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 *
 */


import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.HashMap;
import java.util.Map;

public class HbaseDTO {

    private String id;

    private String clientId;

    private String date;

    private String uri;

    private String bucket;

    private byte[] binaryData;

    public HbaseDTO() {
    }

    public static Map<byte[],byte[]> ObjectToBinary(HbaseDTO hbaseDTO){
        Map<byte[],byte[]> map = new HashMap<>();
        if (StringUtils.isNotEmpty( hbaseDTO.getId() )){
            map.put(Bytes.toBytes("id"),Bytes.toBytes(hbaseDTO.getId()));
        }
        if (StringUtils.isNotEmpty(hbaseDTO.getClientId())){
            map.put(Bytes.toBytes("clientId"),Bytes.toBytes(hbaseDTO.getClientId()));
        }
        if (StringUtils.isNotEmpty((hbaseDTO.getDate()))){
            map.put(Bytes.toBytes("date"),Bytes.toBytes(hbaseDTO.getDate()));
        }
        if (StringUtils.isNotEmpty((hbaseDTO.getUri()))){
            map.put(Bytes.toBytes("uri"),Bytes.toBytes(hbaseDTO.getUri()));
        }
        if (StringUtils.isNotEmpty(hbaseDTO.getBucket())){
            map.put(Bytes.toBytes("bucket"),Bytes.toBytes(hbaseDTO.getBucket()));
        }

        if (null != hbaseDTO.getBinaryData() && hbaseDTO.getBinaryData().length > 0 ){
            map.put(Bytes.toBytes("binaryData"),hbaseDTO.getBinaryData());
        }
        return map;
    }

    public static HbaseDTO BinaryToObject(Result result){
        HbaseDTO object  = new HbaseDTO();
        object.setId(Bytes.toString(result.getRow()));
        for (Cell cell: result.rawCells()) {
            String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
            switch (qualifier){
                case "clientId":
                    object.setClientId(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "date":
                    object.setDate(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "uri":
                    object.setUri(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "bucket":
                    object.setBucket(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "binaryData":
                    object.setBinaryData(CellUtil.cloneValue(cell));
                    break;
                default:
                    break;
            }
        }
        return object;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }
}
