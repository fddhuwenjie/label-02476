package com.yuxuanlei.service;

import com.yuxuanlei.entity.LibraryCard;
import com.yuxuanlei.enums.CardStatus;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.LibraryCardMapper;
import com.yuxuanlei.util.MyBatisUtil;
import com.yuxuanlei.util.PageResult;
import com.yuxuanlei.util.ValidationUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * 借书证服务类
 */
public class LibraryCardService {
    
    private static final Logger logger = LoggerFactory.getLogger(LibraryCardService.class);
    
    /**
     * 为学生办理借书证（1:1 级联）
     */
    public Long issueCard(LibraryCard card) {
        ValidationUtil.validateLibraryCard(card);
        logger.info("为学生办理借书证：studentId={}", card.getStudentId());
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            LibraryCardMapper mapper = sqlSession.getMapper(LibraryCardMapper.class);
            
            // 检查是否已有借书证
            LibraryCard existing = mapper.selectByStudentId(card.getStudentId());
            if (existing != null) {
                throw new DuplicateDataException("该学生已有借书证");
            }
            
            // 设置默认值
            if (card.getIssueDate() == null) {
                card.setIssueDate(LocalDate.now());
            }
            if (card.getExpireDate() == null) {
                card.setExpireDate(LocalDate.now().plusYears(4));
            }
            if (card.getStatus() == null) {
                card.setStatus(CardStatus.ACTIVE);
            }
            
            mapper.insert(card);
            sqlSession.commit();
            return card.getId();
        } catch (DuplicateDataException e) {
            sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            sqlSession.rollback();
            logger.error("办理借书证失败", e);
            throw new BusinessException("办理借书证失败", e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 根据学生ID查询借书证
     */
    public LibraryCard getCardByStudentId(Long studentId) {
        ValidationUtil.validateId(studentId, "学生ID");
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            LibraryCardMapper mapper = sqlSession.getMapper(LibraryCardMapper.class);
            return mapper.selectByStudentId(studentId);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 续期借书证
     */
    public boolean renewCard(Long id, int years) {
        ValidationUtil.validateId(id, "借书证ID");
        logger.info("续期借书证：ID={}, years={}", id, years);
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            LibraryCardMapper mapper = sqlSession.getMapper(LibraryCardMapper.class);
            LibraryCard card = mapper.selectById(id);
            if (card == null) {
                throw new DataNotFoundException("借书证不存在，ID：" + id);
            }
            card.setExpireDate(card.getExpireDate().plusYears(years));
            int rows = mapper.updateById(card);
            sqlSession.commit();
            return rows > 0;
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            sqlSession.rollback();
            logger.error("续期失败", e);
            throw new BusinessException("续期失败", e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 更新借书证状态
     */
    public boolean updateCardStatus(Long id, String status) {
        ValidationUtil.validateId(id, "借书证ID");
        CardStatus.from(status);  // 校验并解析，非法则抛异常
        logger.info("更新借书证状态：ID={}, status={}", id, status);
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            LibraryCardMapper mapper = sqlSession.getMapper(LibraryCardMapper.class);
            LibraryCard card = mapper.selectById(id);
            if (card == null) {
                throw new DataNotFoundException("借书证不存在，ID：" + id);
            }
            card.setStatus(CardStatus.from(status));
            int rows = mapper.updateById(card);
            sqlSession.commit();
            return rows > 0;
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            sqlSession.rollback();
            logger.error("更新状态失败", e);
            throw new BusinessException("更新状态失败", e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 分页查询借书证列表
     */
    public PageResult<LibraryCard> listCards(int pageNum, int pageSize) {
        ValidationUtil.validatePageParams(pageNum, pageSize);
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            LibraryCardMapper mapper = sqlSession.getMapper(LibraryCardMapper.class);
            int offset = (pageNum - 1) * pageSize;
            List<LibraryCard> records = mapper.selectList(offset, pageSize);
            long total = mapper.selectCount();
            return new PageResult<>(records, total, pageNum, pageSize);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 根据ID查询借书证
     */
    public LibraryCard getCardById(Long id) {
        ValidationUtil.validateId(id, "借书证ID");
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            LibraryCardMapper mapper = sqlSession.getMapper(LibraryCardMapper.class);
            return mapper.selectById(id);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 根据ID查询借书证（含关联学生信息，双向 1:1）
     */
    public LibraryCard getCardByIdWithStudent(Long id) {
        ValidationUtil.validateId(id, "借书证ID");
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            LibraryCardMapper mapper = sqlSession.getMapper(LibraryCardMapper.class);
            return mapper.selectByIdWithStudent(id);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 根据学生ID查询借书证（含关联学生信息）
     */
    public LibraryCard getCardByStudentIdWithStudent(Long studentId) {
        ValidationUtil.validateId(studentId, "学生ID");
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            LibraryCardMapper mapper = sqlSession.getMapper(LibraryCardMapper.class);
            return mapper.selectByStudentIdWithStudent(studentId);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
}
