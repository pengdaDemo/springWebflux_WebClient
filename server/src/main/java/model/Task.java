/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */

package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫任务
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    /**
     * 任务ID
     */
    @JsonProperty("_id")
    public String id;

    /**
     * 采集源ID
     */
    public String seedId = "";

    /**
     * 列表页面解析规则 ID
     */
    public String seedExtractRuleId = "";

    /**
     * 数据页面解析规则 ID
     */
    public String profitExtractRuleId = "";

    /**
     * 页面编码
     */
    public String charset = "utf-8";

    /**
     * 任务类型
     */
    public TaskType taskType = TaskType.ENTITY;

    /**
     * 是否是测试任务
     * 测试任务不进行持久化，优先级最高
     */
    public boolean test;

    /**
     * 任务蔓延层数
     */
    public int level;


    /**
     * 地址
     */
    public String url = "";

    /**
     * 方法
     */
    public String method = "GET";

    /**
     * 请求头
     */
    public Map<String, String> requestHeaders = new HashMap<>();

    /**
     * 请求参数
     * post，put 使用x-wwww-form-urlencoded 方式编码提交的参数
     */
    public Map<String, String> requestParams = new HashMap<>();

    /**
     * post,put 提交字符数据
     */
    public String requestBodyRaw;

    /**
     * post,put 提交二进制数据
     */
    public String requestBodyBinary;

    /**
     * http 响应码
     */
    public int statusCode;

    /**
     * http 响应头
     */
    public Map<String, String> responseHeaders = new HashMap<>();

    /**
     * 响应内容
     */
    public byte[] responseContent;

    /**
     * 上一级任务携带的参数
     */
    public Map<String, String> parentTask = new HashMap<>();

    /**
     * 错误信息，执行错误，解析错误
     */
    public String failMsg = "";

    /**
     * 错误信息标签
     */
    public String failMsgTag = "";

    /**
     * 同一域名任务采集时间间隔(单位:秒)
     */
    public long interval;

    /**
     * 地域
     */
    public String region = "JN";

    /**
     * 任务状态
     */
    public String taskStage ="INIT";

    /**
     * 采集客户端 id
     */
    public String clientId = "";

    /**
     * 重试次数
     */
    public int retryTimes;

    /**
     * 优先级，数字越大表示优先级别越高
     */
    public int priority;

    /**
     * 任务错误类型
     */
    public String taskErrorType;

    /**
     * 是否过滤。　true 过滤。　false 不过滤
     */
    public boolean filter = true;

    /**
     * 数据是否过滤。　true 过滤。　false 不过滤
     */
    public boolean dataFilter = true;

    /**
     * 错误统计信息 id
     */
    public String parseErrorCountItemId = "";

    /**
     * 父级任务 id
     */
    public String parentTaskId = "";

    /**
     * 采集批次 id
     */
    public String crawlBatchId = "";

    /**
     * 蔓延任务数量
     */
    public int parseTaskCount;

    /**
     * 解析出的数据数
     */
    public int parseDataCount;

    /**
     * 任务排序值，值越小，任务越先被发布
     */
    public long order = TaskOrderLevel.default_20000;

    /**
     * 任务排序级别
     */
    public long taskOrderLevel = TaskOrderLevel.default_20000;

    /**
     * 是否是采集批次生成的原始任务，与蔓延任务做区分
     */
    public boolean batchSourceTask;

    /**
     * 是否是文件, true 是，false 否
     */
    public boolean file;

    /**
     * 任务创建时间
     */
    public long createTime;

    /**
     * 任务更新时间
     */
    public long updateTime;

    /**
     * 文件标题
     */
    public String fileName;

    /**
     * 文件所在页面url
     */
    public String originUrl;

    /**
     * 解析内容限制大小（M）
     */
    public long parseLimit = 10;

    /**
     * 过期索引
     */
    public Date expireIndexDate = new Date();


    /**
     * 深度复制自身，返回复制出来的对象，修改复制出来的对象不会影响原有对象
     * 复制出来的对象会清除采集响应的相关信息
     *
     * @return 复制出来的 task 对象
     */
    public Task copy() {
        try {
            Task result = new Task();
            result.seedId = this.seedId;
            result.seedExtractRuleId = this.seedExtractRuleId;
            result.profitExtractRuleId = this.profitExtractRuleId;
            result.charset = this.charset;
            result.taskType = TaskType.ENTITY;
            result.test = this.test;
            result.level = this.level;
            result.requestHeaders = this.requestHeaders;
            result.requestParams = this.requestParams;
            result.requestBodyRaw = this.requestBodyRaw;
            result.requestBodyBinary = this.requestBodyBinary;
            result.parentTask = new HashMap<>();
            result.interval = this.interval;
            result.region = this.region;
            result.priority = this.priority;
            result.filter = this.filter;
            result.dataFilter = this.dataFilter;
            result.parentTaskId = this.id;
            result.crawlBatchId = this.crawlBatchId;
            result.parseLimit = this.parseLimit;
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("copy task error, " + ex.getMessage(), ex);
        }
    }

    /**
     * 深度复制自身，返回复制出来的对象，修改复制出来的对象不会影响原有对象
     * 复制出来的对象会清除采集响应的相关信息以及http请求参数
     *
     * @return 复制出来的 task 对象
     */
    public Task copyAndClearRequestParas() {
        try {
            Task result = this.copy();
            result.requestParams = new HashMap<>();
            result.requestBodyBinary = null;
            result.requestBodyRaw = null;
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("copy task error, " + ex.getMessage(), ex);
        }
    }



    /**
     * 获取任务采集回来的网页源文本
     *
     * @return
     */
    public String contentToHtml() {
        try {
            if (this.responseContent == null) {
                return "";
            }
            return StringUtils.toEncodedString(this.responseContent, Charset.forName(this.charset));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getParseLimit() {
        return parseLimit;
    }

    public void setParseLimit(long parseLimit) {
        this.parseLimit = parseLimit;
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

    public String getSeedExtractRuleId() {
        return seedExtractRuleId;
    }

    public void setSeedExtractRuleId(String seedExtractRuleId) {
        this.seedExtractRuleId = seedExtractRuleId;
    }

    public String getProfitExtractRuleId() {
        return profitExtractRuleId;
    }

    public void setProfitExtractRuleId(String profitExtractRuleId) {
        this.profitExtractRuleId = profitExtractRuleId;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    public String getRequestBodyRaw() {
        return requestBodyRaw;
    }

    public void setRequestBodyRaw(String requestBodyRaw) {
        this.requestBodyRaw = requestBodyRaw;
    }

    public String getRequestBodyBinary() {
        return requestBodyBinary;
    }

    public void setRequestBodyBinary(String requestBodyBinary) {
        this.requestBodyBinary = requestBodyBinary;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public byte[] getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(byte[] responseContent) {
        this.responseContent = responseContent;
    }

    public Map<String, String> getParentTask() {
        return parentTask;
    }

    public void setParentTask(Map<String, String> parentTask) {
        this.parentTask = parentTask;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    public String getFailMsgTag() {
        return failMsgTag;
    }

    public void setFailMsgTag(String failMsgTag) {
        this.failMsgTag = failMsgTag;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean isDataFilter() {
        return dataFilter;
    }

    public void setDataFilter(boolean dataFilter) {
        this.dataFilter = dataFilter;
    }

    public String getParseErrorCountItemId() {
        return parseErrorCountItemId;
    }

    public void setParseErrorCountItemId(String parseErrorCountItemId) {
        this.parseErrorCountItemId = parseErrorCountItemId;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getCrawlBatchId() {
        return crawlBatchId;
    }

    public void setCrawlBatchId(String crawlBatchId) {
        this.crawlBatchId = crawlBatchId;
    }

    public int getParseTaskCount() {
        return parseTaskCount;
    }

    public void setParseTaskCount(int parseTaskCount) {
        this.parseTaskCount = parseTaskCount;
    }

    public int getParseDataCount() {
        return parseDataCount;
    }

    public void setParseDataCount(int parseDataCount) {
        this.parseDataCount = parseDataCount;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public long getTaskOrderLevel() {
        return taskOrderLevel;
    }

    public void setTaskOrderLevel(long taskOrderLevel) {
        this.taskOrderLevel = taskOrderLevel;
    }

    public boolean isBatchSourceTask() {
        return batchSourceTask;
    }

    public void setBatchSourceTask(boolean batchSourceTask) {
        this.batchSourceTask = batchSourceTask;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
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

    public Date getExpireIndexDate() {
        return expireIndexDate;
    }

    public void setExpireIndexDate(Date expireIndexDate) {
        this.expireIndexDate = expireIndexDate;
    }
}
