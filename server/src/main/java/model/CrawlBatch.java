package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 采集批次类
 *
 * @author 申成
 */
@Document(collection = "_sys_crawlBatch")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrawlBatch {
    public CrawlBatch(String id, String seedId, int batchSourceTaskCount, int sourceErrorTaskCount, int parseTaskCount, long seedSchedule, CrawlBatchType type) {
        this.id = id;
        this.seedId = seedId;
        this.userName = "crawler";
        this.crawlBatchType = type;
        this.batchSourceTaskCount = batchSourceTaskCount;
        this.sourceErrorTaskCount = sourceErrorTaskCount;
        this.parseTaskCount = parseTaskCount;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
        this.seedSchedule = seedSchedule;
        this.region = "JN";
    }

    @JsonProperty("_id")
    public String id;

    /**
     * 采集源 id
     */
    public String seedId;

    /**
     * 采集源名称
     */
    public String seedName;

    /**
     * 用户名称
     */
    public String userName;

    /**
     * 采集批次类型
     */
    public CrawlBatchType crawlBatchType;

    /**
     * 批次调度产生的初始调度任务数量
     */
    public int batchSourceTaskCount;
    /**
     * 调度任务出错数量
     * */
    public int sourceErrorTaskCount;

    /**
     * 已完成采集的批次调度产生的初始调度任务数量
     */
    public int batchSourceTaskFinishedCount;

    /**
     * 蔓延任务数量
     */
    public int parseTaskCount;

    /**
     * 解析数据数量
     */
    public int parseDataCount;

    /**
     * 已完成采集蔓延任务数量
     */
    public int parseTaskFinishedCount;

    /**
     * 过滤任务数量
     */
    public int filterTaskCount;

    /**
     * 过滤数据数量
     */
    public int filterDataCount;

    /**
     * 新增数据数量
     */
    public int increaseDataCount;

    /**
     * 更新数据数量
     */
    public int updateDataCount;

    /**
     * 解析出错任务数量
     */
    public int parseErrorTaskCount;

    /**
     * 存储出错数据数量
     */
    public int dataStoreErrorCount;

    /**
     * 创建时间
     */
    public long createTime;

    /**
     * 更新时间
     */
    public long updateTime;

    /**
     * 采集批次是否已经完成
     */
    public boolean finished;

    /**
     * 采集源的调度间隔
     */
    public long seedSchedule;

    public int getCurrentUpdatePage() {
        return currentUpdatePage;
    }

    public void setCurrentUpdatePage(int currentUpdatePage) {
        this.currentUpdatePage = currentUpdatePage;
    }

    /**
     * 当前更新采集页码
     */
    public int currentUpdatePage;

    /**
     * 地域
     */
    public String region = "JN";

    public boolean isInterval;

    /**
     * 过期索引
     */
    public Date expireIndexDate = new Date();

    public CrawlBatch() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeedId() {
        return seedId;
    }

    public void setSeedId(String seedId) {
        this.seedId = seedId;
    }

    public int getBatchSourceTaskCount() {
        return batchSourceTaskCount;
    }

    public void setBatchSourceTaskCount(int batchSourceTaskCount) {
        this.batchSourceTaskCount = batchSourceTaskCount;
    }

    public int getBatchSourceTaskFinishedCount() {
        return batchSourceTaskFinishedCount;
    }

    public void setBatchSourceTaskFinishedCount(int batchSourceTaskFinishedCount) {
        this.batchSourceTaskFinishedCount = batchSourceTaskFinishedCount;
    }

    public int getParseDataCount() {
        return parseDataCount;
    }

    public void setParseDataCount(int parseDataCount) {
        this.parseDataCount = parseDataCount;
    }

    public int getParseTaskCount() {
        return parseTaskCount;
    }

    public void setParseTaskCount(int parseTaskCount) {
        this.parseTaskCount = parseTaskCount;
    }

    public int getParseTaskFinishedCount() {
        return parseTaskFinishedCount;
    }

    public void setParseTaskFinishedCount(int parseTaskFinishedCount) {
        this.parseTaskFinishedCount = parseTaskFinishedCount;
    }

    public int getFilterTaskCount() {
        return filterTaskCount;
    }

    public void setFilterTaskCount(int filterTaskCount) {
        this.filterTaskCount = filterTaskCount;
    }

    public int getFilterDataCount() {
        return filterDataCount;
    }

    public void setFilterDataCount(int filterDataCount) {
        this.filterDataCount = filterDataCount;
    }

    public int getIncreaseDataCount() {
        return increaseDataCount;
    }

    public void setIncreaseDataCount(int increaseDataCount) {
        this.increaseDataCount = increaseDataCount;
    }

    public int getUpdateDataCount() {
        return updateDataCount;
    }

    public void setUpdateDataCount(int updateDataCount) {
        this.updateDataCount = updateDataCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public CrawlBatchType getCrawlBatchType() {
        return crawlBatchType;
    }

    public void setCrawlBatchType(CrawlBatchType crawlBatchType) {
        this.crawlBatchType = crawlBatchType;
    }

    public int getParseErrorTaskCount() {
        return parseErrorTaskCount;
    }

    public void setParseErrorTaskCount(int parseErrorTaskCount) {
        this.parseErrorTaskCount = parseErrorTaskCount;
    }

    public int getDataStoreErrorCount() {
        return dataStoreErrorCount;
    }

    public void setDataStoreErrorCount(int dataStoreErrorCount) {
        this.dataStoreErrorCount = dataStoreErrorCount;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Date getExpireIndexDate() {
        return expireIndexDate;
    }

    public void setExpireIndexDate(Date expireIndexDate) {
        this.expireIndexDate = expireIndexDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getSeedSchedule() {
        return seedSchedule;
    }

    public void setSeedSchedule(long seedSchedule) {
        this.seedSchedule = seedSchedule;
    }

    public String getSeedName() {
        return seedName;
    }

    public void setSeedName(String seedName) {
        this.seedName = seedName;
    }

    public boolean isInterval() {
        return isInterval;
    }

    public void setInterval(boolean interval) {
        isInterval = interval;
    }
}