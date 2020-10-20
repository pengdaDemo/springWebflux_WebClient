import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.HashMap;
import java.util.Map;


public class HbaseFileInfo {
    private String id;

    private String md5_id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 爬虫采集源id
     */
    private String seedId;

    /**
     * 源文件所在页面url
     */
    private String pageUrl;

    /**
     * 源文件url
     */
    private String url;

    /**
     * 文件内容是否存在Hbase
     */
    private boolean exist;

    /**
     * hdfs资源路径
     */
    private String uri;

    /**
     * 文件类型
     */
    private String type;

    private long length;

    public HbaseFileInfo() {

    }

    public static Map<byte[],byte[]> ObjectToBinary(HbaseFileInfo hbaseDTO){
        Map<byte[],byte[]> map = new HashMap<>();
        if (StringUtils.isNotEmpty(hbaseDTO.getMd5_id())){
            map.put(Bytes.toBytes("md5_id"),Bytes.toBytes(hbaseDTO.getMd5_id()));
        }
        if (StringUtils.isNotEmpty((hbaseDTO.getName()))){
            map.put(Bytes.toBytes("name"),Bytes.toBytes(hbaseDTO.getName()));
        }
        if (StringUtils.isNotEmpty((hbaseDTO.getUri()))){
            map.put(Bytes.toBytes("uri"),Bytes.toBytes(hbaseDTO.getUri()));
        }
        if (StringUtils.isNotEmpty(hbaseDTO.getSeedId())){
            map.put(Bytes.toBytes("seedId"),Bytes.toBytes(hbaseDTO.getSeedId()));
        }
        if (StringUtils.isNotEmpty(hbaseDTO.getPageUrl())){
            map.put(Bytes.toBytes("pageUrl"),Bytes.toBytes(hbaseDTO.getPageUrl()));
        }
        if (StringUtils.isNotEmpty(hbaseDTO.getUrl())){
            map.put(Bytes.toBytes("url"),Bytes.toBytes(hbaseDTO.getUrl()));
        }
        map.put(Bytes.toBytes("exist"),Bytes.toBytes(hbaseDTO.isExist()));
        if (StringUtils.isNotEmpty(hbaseDTO.getType())){
            map.put(Bytes.toBytes("type"),Bytes.toBytes(hbaseDTO.getType()));
        }
        map.put(Bytes.toBytes("length"),Bytes.toBytes(hbaseDTO.getLength()));
        return map;
    }


    public static HbaseFileInfo BinaryToObject(Result result){
        HbaseFileInfo object  = new HbaseFileInfo();
        object.setId(Bytes.toString(result.getRow()));
        for (Cell cell: result.rawCells()) {
            String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
            switch (qualifier){
                case "md5_id":
                    object.setMd5_id(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "name":
                    object.setName(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "seedId":
                    object.setSeedId(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "pageUrl":
                    object.setPageUrl(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "url":
                    object.setUrl(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "exist":
                    object.setExist(Bytes.toBoolean(CellUtil.cloneValue(cell)));
                    break;
                case "uri":
                    object.setUri(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "type":
                    object.setType(Bytes.toString(CellUtil.cloneValue(cell)));
                    break;
                case "length":
                    object.setLength(Bytes.toLong(CellUtil.cloneValue(cell)));
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

    public String getMd5_id() {
        return md5_id;
    }

    public void setMd5_id(String md5_id) {
        this.md5_id = md5_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeedId() {
        return seedId;
    }

    public void setSeedId(String seedId) {
        this.seedId = seedId;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
