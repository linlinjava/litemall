package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;

/**
 * 订单表（litemall_order）的 MyBatis Mapper 接口。
 *
 * <p>方法由 MyBatis Generator 生成，分两类：xxxByExample 面向动态条件查询与批量操作，
 * xxxByPrimaryKey 面向主键单条操作。本接口只负责 SQL 映射，
 * 订单状态是否允许流转等业务规则在 LitemallOrderService 及其上层实现。
 *
 * <p>注意：按主键或按条件的查询不会自动过滤 deleted 字段，
 * 需要排除已逻辑删除的订单时，请使用 xxxWithLogicalDelete 系列或自行带 deleted 条件。
 *
 * @author lvweihao0403
 * @since 1.0.0
 */
public interface LitemallOrderMapper {
    /**
     * 按条件统计订单数量。
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @return 匹配的记录数
     */
    long countByExample(LitemallOrderExample example);

    /**
     * 按条件物理删除订单。
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @return 影响行数；业务删除请改用 logicalDeleteByExample
     */
    int deleteByExample(LitemallOrderExample example);

    /**
     * 按主键物理删除订单。
     * @param id 订单主键
     * @return 影响行数；业务删除请改用 logicalDeleteByPrimaryKey
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条订单，写入全部字段（null 会覆盖数据库默认值）。
     * @param record 订单实体，非 null
     * @return 影响行数；业务插入请改用 insertSelective
     */
    int insert(LitemallOrder record);

    /**
     * 插入一条订单，只写入对象中非 null 的字段，保留数据库默认值。
     * @param record 订单实体，非 null
     * @return 影响行数；业务写入推荐使用本方法
     */
    int insertSelective(LitemallOrder record);

    /**
     * 按条件查询单条订单。
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @return 订单；无匹配时返回 null
     */
    LitemallOrder selectOneByExample(LitemallOrderExample example);

    /**
     * 按条件查询单条订单，只返回指定列。
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @param selective 需要返回的列集合，不传表示全部列
     * @return 订单；无匹配时返回 null
     */
    LitemallOrder selectOneByExampleSelective(@Param("example") LitemallOrderExample example, @Param("selective") LitemallOrder.Column ... selective);

    /**
     * 按条件查询订单列表，只返回指定列，用于避免查询大字段。
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @param selective 需要返回的列集合，不传表示全部列
     * @return 订单列表
     */
    List<LitemallOrder> selectByExampleSelective(@Param("example") LitemallOrderExample example, @Param("selective") LitemallOrder.Column ... selective);

    /**
     * 按条件查询订单列表。
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @return 订单列表
     */
    List<LitemallOrder> selectByExample(LitemallOrderExample example);

    /**
     * 按主键查询单条订单，只返回指定列。
     * @param id 订单主键
     * @param selective 需要返回的列集合，不传表示全部列
     * @return 订单；不存在时返回 null
     */
    LitemallOrder selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallOrder.Column ... selective);

    /**
     * 按主键查询单条订单。
     *
     * <p>注意：本方法不过滤 deleted 字段，已逻辑删除的订单同样会返回；
     * 下单校验、支付回调等场景应改用带 deleted = 0 条件的查询。
     * @param id 订单主键
     * @return 订单；记录不存在时返回 null，调用方必须判空
     */
    LitemallOrder selectByPrimaryKey(Integer id);

    /**
     * 按主键查询单条订单，并显式指定是否包含已逻辑删除的记录。
     * @param id 订单主键
     * @param andLogicalDeleted true 只查已删除记录，false 只查未删除记录
     * @return 订单；不存在时返回 null
     */
    LitemallOrder selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * 按条件更新订单，只更新对象中非 null 的字段。
     * @param record 订单实体，非 null
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @return 影响行数
     */
    int updateByExampleSelective(@Param("record") LitemallOrder record, @Param("example") LitemallOrderExample example);

    /**
     * 按条件更新订单全部字段，未赋值字段会被写成 null，慎用。
     * @param record 订单实体，非 null
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @return 影响行数；业务更新推荐 updateByExampleSelective
     */
    int updateByExample(@Param("record") LitemallOrder record, @Param("example") LitemallOrderExample example);

    /**
     * 按主键更新订单，只更新对象中非 null 的字段。
     * @param record 订单实体，非 null
     * @return 影响行数；业务更新推荐使用本方法
     */
    int updateByPrimaryKeySelective(LitemallOrder record);

    /**
     * 按主键更新订单全部字段。
     * @param record 订单实体，非 null
     * @return 影响行数；未赋值字段会被覆盖，慎用
     */
    int updateByPrimaryKey(LitemallOrder record);

    /**
     * 按条件逻辑删除订单（deleted 置 1），保留数据用于对账与审计。
     * @param example 查询条件（LitemallOrderExample），可为 null
     * @return 影响行数
     */
    int logicalDeleteByExample(@Param("example") LitemallOrderExample example);

    /**
     * 按主键逻辑删除订单（deleted 置 1）。
     * @param id 订单主键
     * @return 影响行数
     */
    int logicalDeleteByPrimaryKey(Integer id);
}