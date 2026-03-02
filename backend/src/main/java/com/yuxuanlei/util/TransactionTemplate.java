package com.yuxuanlei.util;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一事务执行模板：在单次 SqlSession 内执行操作，成功 commit，异常 rollback，最后关闭 session。
 * <p>
 * 使用方式：
 * <pre>
 * TransactionTemplate.executeWrite(session -> {
 *     XxxMapper mapper = session.getMapper(XxxMapper.class);
 *     mapper.insert(entity);
 *     return entity.getId();
 * });
 * </pre>
 */
public final class TransactionTemplate {

    private static final Logger logger = LoggerFactory.getLogger(TransactionTemplate.class);

    private TransactionTemplate() {}

    /**
     * 执行写操作（需要 commit）：插入、更新、删除。
     * 成功后 commit，异常时 rollback。
     *
     * @param callback 在 SqlSession 上执行的逻辑，返回结果
     * @return 回调返回值
     */
    public static <T> T executeWrite(TransactionCallback<T> callback) {
        SqlSession session = null;
        try {
            session = MyBatisUtil.getSqlSession();
            T result = callback.doInTransaction(session);
            session.commit();
            return result;
        } catch (Exception e) {
            if (session != null) {
                session.rollback();
                logger.debug("事务回滚: {}", e.getMessage());
            }
            throw e;
        } finally {
            MyBatisUtil.closeSqlSession(session);
        }
    }

    /**
     * 执行读操作（无需 commit，失败不回滚）。
     *
     * @param callback 在 SqlSession 上执行的逻辑
     * @return 回调返回值
     */
    public static <T> T executeRead(TransactionCallback<T> callback) {
        SqlSession session = null;
        try {
            session = MyBatisUtil.getSqlSession();
            return callback.doInTransaction(session);
        } finally {
            MyBatisUtil.closeSqlSession(session);
        }
    }

    @FunctionalInterface
    public interface TransactionCallback<T> {
        T doInTransaction(SqlSession session);
    }
}
