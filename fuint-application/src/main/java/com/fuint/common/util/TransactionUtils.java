package com.fuint.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 事务工具类
 *
 * 说明：短信、微信订阅消息、打印等外部 HTTP 调用必须放到事务提交之后执行，
 * 否则外部接口耗时会把事务拉长，长时间占用数据库行锁（如 mt_user），
 * 导致其它请求出现 Lock wait timeout exceeded
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class TransactionUtils {

    private static final Logger logger = LoggerFactory.getLogger(TransactionUtils.class);

    /**
     * 在数据库事务提交之后执行任务；当前没有事务时立即执行。
     * 任务内部异常只记录日志，不影响主流程。
     *
     * @param task 待执行任务
     */
    public static void runAfterCommit(Runnable task) {
        if (task == null) {
            return;
        }
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        task.run();
                    } catch (Exception e) {
                        logger.error("事务提交后执行任务出错啦，message = {}", e.getMessage());
                    }
                }
            });
        } else {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("执行任务出错啦，message = {}", e.getMessage());
            }
        }
    }
}
