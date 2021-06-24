/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */

package model;

/**
 * 根据任务类型来区分任务优先级
 * LIST 低优先级
 * ENTITY 中优先级
 */
public enum TaskType {
    /**
     * 调度任务
     */
    SCHEDULE,

    /**
     * 数据列表任务
     */
    LIST,

    /**
     * 数据实体任务
     */
    ENTITY
}
