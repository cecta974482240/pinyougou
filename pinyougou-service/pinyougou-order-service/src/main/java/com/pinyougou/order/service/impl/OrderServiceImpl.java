package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-03-20<p>
 */
@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayLogMapper payLogMapper;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void save(Order order) {
        try {

            // 1. 从Redis数据库获取该用户的购物车
            List<Cart> carts = (List<Cart>) redisTemplate
                    .boundValueOps("cart_" + order.getUserId()).get();

            // 定义支付总金额
            double totalMoney = 0;
            // 封装多个订单id
            String orderIds = "";
            
            // 2. 往tb_order表插入数据(一个商家的购物车产生一个订单)
            for (Cart cart : carts) {
                // 一个cart代表一个商家的购物车(产生一个订单)
                Order order1 = new Order();
                // 生成主键id
                long orderId = idWorker.nextId();
                // 设置订单id
                order1.setOrderId(orderId);
                // 设置支付方式
                order1.setPaymentType(order.getPaymentType());
                // 设置状态码: 1 未付款
                order1.setStatus("1");
                // 设置订单创建时间
                order1.setCreateTime(new Date());
                // 设置订单修改时间
                order1.setUpdateTime(order1.getCreateTime());
                // 设置用户id
                order1.setUserId(order.getUserId());
                // 设置收件地址
                order1.setReceiverAreaName(order.getReceiverAreaName());
                // 设置收件人手机号码
                order1.setReceiverMobile(order.getReceiverMobile());
                // 设置收件人姓名
                order1.setReceiver(order.getReceiver());
                // 设置订单来源
                order1.setSourceType(order.getSourceType());
                // 设置商家id
                order1.setSellerId(cart.getSellerId());

                // 订单总金额
                double money = 0;

                // 3. 往tb_order_item表插入数据
                for (OrderItem orderItem : cart.getOrderItems()) {

                    // 生成主键id
                    orderItem.setId(idWorker.nextId());
                    // 设置关联的订单id
                    orderItem.setOrderId(orderId);

                    // 累计订单的金额
                    money += orderItem.getTotalFee().doubleValue();
                    // 保存订单明细
                    orderItemMapper.insertSelective(orderItem);
                }

                // 累计订单总金额
                totalMoney += money;
                // 拼接多个订单id
                orderIds += orderId + ",";
                // 设置订单的总金额
                order1.setPayment(new BigDecimal(money));
                // 保存订单
                orderMapper.insertSelective(order1);
            }


            // 往支付日志表中插入数据
            if ("1".equals(order.getPaymentType())){
                // 在线支付
                PayLog payLog = new PayLog();
                // 交易订单号
                payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
                // 创建时间
                payLog.setCreateTime(new Date());
                // 支付的总金额(分)
                payLog.setTotalFee((long)(totalMoney * 100));
                // 用户id
                payLog.setUserId(order.getUserId());
                // 交易状态 (未支付)
                payLog.setTradeState("0");
                // 多个订单id，组成一次支付
                payLog.setOrderList(orderIds.substring(0, orderIds.length() - 1));
                // 支付类型
                payLog.setPayType(order.getPaymentType());

                // 插入数据
                payLogMapper.insertSelective(payLog);

                // 把PayLog对象存储到Redis数据库
                redisTemplate.boundValueOps("payLog_"
                        + order.getUserId()).set(payLog);
            }

            // 4. 从Redis数据库中删除用户的购物车
            redisTemplate.delete("cart_" + order.getUserId());

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Order order) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public PageInfo<Order> findByPage(String userId, Integer page, Integer rows) {
        try{
            // 开始分页
            PageInfo<Order> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            findOrder(userId);
                        }
                    });
            return pageInfo;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /** 根据登录用户名，从Redis数据库获取支付日志对象 */
    public PayLog findPayLogFromRedis(String userId){
        try{
            return (PayLog)redisTemplate.boundValueOps("payLog_" + userId).get();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 支付成功，业务处理 */
    public void updateStatus(String outTradeNo, String transactionId){
        try{
            // 1. 修改支付日志表
            PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
            // 支付时间
            payLog.setPayTime(new Date());
            // 交易状态
            payLog.setTradeState("1");
            // 微信支付订单号
            payLog.setTransactionId(transactionId);
            // 修改
            payLogMapper.updateByPrimaryKeySelective(payLog);

            // 2. 修改订单表
            String[] orderIds = payLog.getOrderList().split(",");
            // 循环修改多个订单的付款状态
            for (String orderId : orderIds) {
                Order order = new Order();
                order.setOrderId(Long.valueOf(orderId));
                // 付款时间
                order.setPaymentTime(new Date());
                // 已付款
                order.setStatus("2");
                // 修改
                orderMapper.updateByPrimaryKeySelective(order);
            }

            // 3. 删除支付日志
            redisTemplate.delete("payLog_" + payLog.getUserId());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


  @Override
    public void updateStatusByUser(String outTradeNo, String transactionId,String orderId){
        try{
            Order order = new Order();
            Long orderid = Long.valueOf(orderId);
            Example example = new Example(Order.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderId",orderid );
            order.setStatus("2");
            orderMapper.updateByExampleSelective(order,example );
            PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
            String orderList = payLog.getOrderList();
            String[] split = orderList.split(",");
            if(split.length == 1){
                // 支付时间
                payLog.setPayTime(new Date());
                // 交易状态
                payLog.setTradeState("1");
                // 微信支付订单号
                payLog.setTransactionId(transactionId);
                // 修改
                payLogMapper.updateByPrimaryKeySelective(payLog);
            }else {
                // 支付时间
                PayLog payLog1 = new PayLog();
                payLog1.setPayTime(new Date());
                // 交易状态
                payLog1.setTradeState("1");
                // 微信支付订单号
                payLog1.setTransactionId(transactionId);
                payLog1.setCreateTime(new Date());
                payLog1.setUserId(payLog.getUserId());
                payLog1.setOutTradeNo(idWorker.nextId()+"");

               Example example1 = new Example(Order.class);
                Example.Criteria criteria1 = example1.createCriteria();
                criteria1.andEqualTo("orderId",orderid );
                List<Order> order1 = orderMapper.selectByExample(example1);
                payLog1.setTotalFee(order1.get(0).getPayment().longValue());
                payLog1.setTransactionId(transactionId);
                payLog1.setPayType("1");
                payLog1.setOrderList(orderId);
                payLogMapper.insertSelective(payLog1);
                ArrayList<String> list = new ArrayList<>();
                for (String s : split) {
                    if(!s.equals(orderId)){
                        list.add(s);
                    }
                }
                String ids = list.toString().replace("[", "").replaceAll("]", "");
                payLog.setOrderList(ids);
                payLogMapper.updateByPrimaryKeySelective(payLog);
                // 3. 删除支付日志
                redisTemplate.delete("payLog_" + payLog.getUserId());
            }


        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> getOrder(String orderId) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        return orderMapper.selectByExample(example);


    }


    @Override
    public List<Order> findOrder(String userId) {
        try{
            Example example = new Example(Order.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",userId );
            example.orderBy("status").asc();
            List<Order> orderList = orderMapper.selectByExample(example);
            for (Order order : orderList) {
                order.setLongid(order.getLongId());
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getOrderId());
                List<OrderItem> orderItems = orderItemMapper.select(orderItem);
                order.setOrderItems(orderItems);
            }
            return orderList;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }



}
