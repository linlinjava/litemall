package org.linlinjava.litemall.db.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.dao.OrderMapper;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;
import org.linlinjava.litemall.db.domain.OrderVo;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 订单数据服务：封装 litemall_order 的读写、分页查询与按状态统计。
 *
 * <p>本类只做数据访问与查询条件组装，不校验状态流转是否合法；
 * 支付、发货、取消等规则由 admin-api / wx-api 层的业务服务调用本类完成。
 *
 * <p>所有查询默认过滤 deleted = 0，删除操作只做逻辑删除（deleted = 1），
 * 保留订单数据用于对账与售后追溯。
 *
 * @author lvweihao0403
 * @since 1.0.0
 */
@Service
public class LitemallOrderService {
    @Resource
    private LitemallOrderMapper litemallOrderMapper;
    @Resource
    private OrderMapper orderMapper;

    /**
     * 新增订单，写入时自动补 addTime 与 updateTime。
     *
     * @param order 订单实体，金额、状态由调用方填写
     * @return 影响行数
     */
    public int add(LitemallOrder order) {
        order.setAddTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return litemallOrderMapper.insertSelective(order);
    }

    /**
     * 统计某用户的订单总数。
     *
     * @param userId 用户 ID
     * @return 该用户的有效订单数（已过滤逻辑删除）
     */
    public int count(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    /**
     * 按订单主键查询（不区分用户）。
     *
     * <p>供支付回调、定时任务等内部流程使用，不过滤逻辑删除；
     * 面向用户的查询请使用 findById(userId, orderId)。
     *
     * @param orderId 订单主键
     * @return 订单；不存在时返回 null
     */
    public LitemallOrder findById(Integer orderId) {
        return litemallOrderMapper.selectByPrimaryKey(orderId);
    }

    /**
     * 按“用户 + 订单”查询，保证用户只能查到自己的订单，并过滤已逻辑删除的记录。
     *
     * @param userId  用户 ID
     * @param orderId 订单主键
     * @return 订单；不属于该用户或已删除时返回 null
     */
    public LitemallOrder findById(Integer userId, Integer orderId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andIdEqualTo(orderId).andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return litemallOrderMapper.selectOneByExample(example);
    }

    /**
     * 生成指定位数的纯数字随机串，仅用于拼接订单编号。
     *
     * @param num 位数
     * @return 由 0-9 组成的定长字符串
     */
    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 统计某用户下指定订单编号的记录数，用于生成编号时查重。
     *
     * @param userId  用户 ID
     * @param orderSn 订单编号
     * @return 匹配的记录数，0 表示编号可用
     */
    public int countByOrderSn(Integer userId, String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    /**
     * 生成订单编号：yyyyMMdd + 6 位随机数字。
     *
     * <p>生成后会查询该用户下是否已存在同一编号，重复则重新生成。
     * 已知风险：两次校验之间若有并发插入，仍可能产生重复编号，
     * 且 litemall_order.order_sn 目前没有唯一索引兜底；
     * 后续应改为“日期 + 自增序列”等强唯一方案（原 TODO 已转入本注释）。
     *
     * @param userId 下单用户 ID，用于限定编号查重范围
     * @return 订单编号，形如 20261017004217
     */
    public String generateOrderSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        String orderSn = now + getRandomNum(6);
        while (countByOrderSn(userId, orderSn) != 0) {
            orderSn = now + getRandomNum(6);
        }
        return orderSn;
    }

    /**
     * 按状态集合分页查询某用户的订单，默认按下单时间倒序。
     *
     * @param userId      用户 ID
     * @param orderStatus 订单状态集合，null 表示不限状态
     * @param page        页码，从 1 开始
     * @param limit       每页条数
     * @param sort        排序字段，与 order 同时非空时生效
     * @param order       asc 或 desc
     * @return 当前页订单列表
     */
    public List<LitemallOrder> queryByOrderStatus(Integer userId, List<Short> orderStatus, Integer page, Integer limit, String sort, String order) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.setOrderByClause(LitemallOrder.Column.addTime.desc());
        LitemallOrderExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId);
        if (orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return litemallOrderMapper.selectByExample(example);
    }

    /**
     * 后台多条件分页查询订单：用户、订单号、下单时间区间、状态集合。
     *
     * <p>条件之间是“与”的关系；为空的参数不参与筛选。
     *
     * @param userId           用户 ID，可为 null
     * @param orderSn          订单编号，精确匹配
     * @param start            下单时间下限（含）
     * @param end              下单时间上限（含）
     * @param orderStatusArray 状态集合，null 或空表示不限
     * @param page             页码
     * @param limit            每页条数
     * @param sort             排序字段
     * @param order            排序方向
     * @return 当前页订单列表
     */
    public List<LitemallOrder> querySelective(Integer userId, String orderSn, LocalDateTime start, LocalDateTime end, List<Short> orderStatusArray, Integer page, Integer limit, String sort, String order) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnEqualTo(orderSn);
        }
        if(start != null){
            criteria.andAddTimeGreaterThanOrEqualTo(start);
        }
        if(end != null){
            criteria.andAddTimeLessThanOrEqualTo(end);
        }
        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return litemallOrderMapper.selectByExample(example);
    }

    /**
     * 以 updateTime 作为版本号做乐观锁更新，避免并发写覆盖。
     *
     * <p>调用方需传入“读取时的 updateTime”，方法内部会先把它刷新为当前时间再执行更新；
     * 返回 0 表示该订单在读取后被其它事务修改过，应提示用户重试。
     *
     * @param order 待更新订单，必须携带 id 与读取时的 updateTime
     * @return 影响行数，0 表示版本冲突
     */
    public int updateWithOptimisticLocker(LitemallOrder order) {
        LocalDateTime preUpdateTime = order.getUpdateTime();
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateWithOptimisticLocker(preUpdateTime, order);
    }

    /**
     * 按主键更新非 null 字段，不做乐观锁校验。
     *
     * @param order 待更新订单，至少包含 id
     * @return 影响行数
     */
    public int updateSelective(LitemallOrder order) {
        return litemallOrderMapper.updateByPrimaryKeySelective(order);
    }


    /**
     * 逻辑删除订单：只把 deleted 置为 true，数据保留用于对账与售后。
     *
     * @param id 订单主键
     */
    public void deleteById(Integer id) {
        litemallOrderMapper.logicalDeleteByPrimaryKey(id);
    }

    /**
     * 统计全部有效订单数。
     *
     * @return 订单总数（已过滤逻辑删除）
     */
    public int count() {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    /**
     * 查询超过指定分钟仍未支付的订单，供定时任务自动关闭。
     *
     * @param minutes 超时分钟数
     * @return 待关闭的订单列表
     */
    public List<LitemallOrder> queryUnpaid(int minutes) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    /**
     * 查询发货后超过指定天数仍未确认收货的订单，供系统自动确认收货。
     *
     * @param days 允许的最大未确认天数
     * @return 待自动确认的订单列表
     */
    public List<LitemallOrder> queryUnconfirm(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_SHIP).andShipTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    /**
     * 按订单编号查询（全局，不区分用户），供支付回调按商户订单号找回订单。
     *
     * @param orderSn 订单编号
     * @return 订单；不存在时返回 null
     */
    public LitemallOrder findBySn(String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return litemallOrderMapper.selectOneByExample(example);
    }

    /**
     * 统计用户在“我的订单”各页签的数量，用于页签角标。
     *
     * <p>返回 Map 的键固定为：unpaid 待付款、unship 待发货、
     * unrecv 待收货、uncomment 待评价商品数（累加订单的 comments 字段）。
     * 键名是与前端约定的契约，不可随意改名。
     *
     * @param userId 用户 ID
     * @return 上述 4 个键的统计结果
     */
    public Map<Object, Object> orderInfo(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        List<LitemallOrder> orders = litemallOrderMapper.selectByExampleSelective(example, LitemallOrder.Column.orderStatus, LitemallOrder.Column.comments);

        int unpaid = 0;
        int unship = 0;
        int unrecv = 0;
        int uncomment = 0;
        for (LitemallOrder order : orders) {
            if (OrderUtil.isCreateStatus(order)) {
                unpaid++;
            } else if (OrderUtil.isPayStatus(order)) {
                unship++;
            } else if (OrderUtil.isShipStatus(order)) {
                unrecv++;
            } else if (OrderUtil.isConfirmStatus(order) || OrderUtil.isAutoConfirmStatus(order)) {
                uncomment += order.getComments();
            } else {
                // 已取消、已退款、已完成等状态不属于任何待办页签，不计入角标
            }
        }

        Map<Object, Object> orderInfo = new HashMap<Object, Object>();
        orderInfo.put("unpaid", unpaid);
        orderInfo.put("unship", unship);
        orderInfo.put("unrecv", unrecv);
        orderInfo.put("uncomment", uncomment);
        return orderInfo;

    }

    /**
     * 查询确认收货超过指定天数但仍有待评价商品的订单，用于关闭评价入口。
     *
     * @param days 允许评价的最大天数
     * @return 待处理的订单列表
     */
    public List<LitemallOrder> queryComment(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andCommentsGreaterThan((short) 0).andConfirmTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    /**
     * 只更新订单的售后状态，避免整行覆盖其它字段。
     *
     * @param orderId      订单主键
     * @param statusReject 售后状态码，取值见 LitemallOrder.aftersaleStatus 注释
     */
    public void updateAftersaleStatus(Integer orderId, Short statusReject) {
        LitemallOrder order = new LitemallOrder();
        order.setId(orderId);
        order.setAftersaleStatus(statusReject);
        order.setUpdateTime(LocalDateTime.now());
        litemallOrderMapper.updateByPrimaryKeySelective(order);
    }


    /**
     * 后台订单列表查询：关联用户昵称与订单商品明细，返回分页 Map。
     *
     * <p>返回结构固定为 list（OrderVo 列表）、total、page、limit、pages 五个键，
     * 与前端表格分页组件约定一致。
     *
     * @param nickname         用户昵称，模糊匹配
     * @param consignee        收货人，模糊匹配
     * @param orderSn          订单编号，精确匹配
     * @param start            下单时间下限
     * @param end              下单时间上限
     * @param orderStatusArray 状态集合
     * @param page             页码
     * @param limit            每页条数
     * @param sort             排序字段
     * @param order            排序方向
     * @return 含 list 与分页信息的 Map
     */
    public Map<String, Object> queryVoSelective(String nickname, String consignee, String orderSn, LocalDateTime start, LocalDateTime end, List<Short> orderStatusArray, Integer page, Integer limit, String sort, String order) {
        List<String> querys = new ArrayList<>(4);
        if (!StringUtils.isEmpty(nickname)) {
            querys.add(" u.nickname like '%" + nickname + "%' ");
        }
        if (!StringUtils.isEmpty(consignee)) {
            querys.add(" o.consignee like '%" + consignee + "%' ");
        }
        if (!StringUtils.isEmpty(orderSn)) {
            querys.add(" o.order_sn = '" + orderSn + "' ");
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (start != null) {
            querys.add(" o.add_time >= '" + df.format(start) + "' ");
        }
        if (end != null) {
            querys.add(" o.add_time < '" + df.format(end) + "' ");
        }
        if (orderStatusArray != null && orderStatusArray.size() > 0) {
            querys.add(" o.order_status in (" + StringUtils.collectionToDelimitedString(orderStatusArray, ",") + ") ");
        }
        querys.add(" o.deleted = 0 and og.deleted = 0 ");
        String query = StringUtils.collectionToDelimitedString(querys, "and");
        String orderByClause = null;
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            orderByClause = "o." + sort + " " + order +", o.id desc ";
        }

        // 后台订单列表分两步查：先用条件筛出订单 id 并分页，再按 id 批量取明细；
        // 原因是订单与订单商品是一对多，直接按明细行分页会让每页订单条数不准
        PageHelper.startPage(page, limit);
        Page<Map> list1 = (Page) orderMapper.getOrderIds(query, orderByClause);
        List<Integer> ids = new ArrayList<>();
        for (Map map : list1) {
            Integer id = (Integer) map.get("id");
            ids.add(id);
        }

        // 第二段：按上一步的 id 集合一次取回订单 + 用户 + 商品明细，避免 N+1 查询
        List<OrderVo> list2 = new ArrayList<>();
        if (!ids.isEmpty()) {
            querys.add(" o.id in (" + StringUtils.collectionToDelimitedString(ids, ",") + ") ");
            query = StringUtils.collectionToDelimitedString(querys, "and");
            list2 = orderMapper.getOrderList(query, orderByClause);
        }
        Map<String, Object> data = new HashMap<String, Object>(5);
        data.put("list", list2);
        data.put("total", list1.getTotal());
        data.put("page", list1.getPageNum());
        data.put("limit", list1.getPageSize());
        data.put("pages", list1.getPages());
        return data;
    }
}
