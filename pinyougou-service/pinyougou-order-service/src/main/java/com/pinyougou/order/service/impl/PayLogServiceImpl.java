package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.PayLogService")
@Transactional
public class PayLogServiceImpl implements PayLogService{


    @Autowired
    private PayLogMapper payLogMapper;


    @Override
    public void save(PayLog payLog) {

    }

    @Override
    public void update(PayLog payLog) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public PayLog findOne(Serializable id) {
        return null;
    }

    @Override
    public List<PayLog> findAll() {
        return null;
    }

    @Override
    public List<PayLog> findByPage(PayLog payLog, int page, int rows) {
        return null;
    }

    @Override
    public PayLog getpayLog(String userId,String orderId) {
        try{
            Example example = new Example(PayLog.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",userId );
            List<PayLog> payLogs = payLogMapper.selectByExample(example);
            for (PayLog payLog : payLogs) {
                String orderList = payLog.getOrderList();
                if(orderList.contains(orderId)){
                    return payLog;
                }
            }
            return null;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
