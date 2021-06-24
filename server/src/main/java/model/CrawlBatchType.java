package model;

/**
 * 采集批次类型
 *
 * @author 2019-05-27
 */
public enum CrawlBatchType {
    /**
     * 历史采集
     */
    HISTORY,

    /**
     * 更新采集
     */
    UPDATE,

    /**
     * 定时采集
     */
    FIX,

    /**
     * 错误补抓
     */
    ERRORRETRY,

    /**
     * 蔓延采集
     */
    SPREAD
}