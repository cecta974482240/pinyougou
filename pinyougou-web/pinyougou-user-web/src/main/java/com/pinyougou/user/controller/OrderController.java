package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.PayLogService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 *
 * @version 1.0
 * <p>File Created at 2019-03-20<p>
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 10000)
    private OrderService orderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    @Reference(timeout = 10000)
    private PayLogService payLogService;





    /** 生成微信支付的二维码 */
    @GetMapping("/genPayCode")
    public Map<String,Object> genPayCode(HttpServletRequest request,String orderId){
        List<Order> orderList =  orderService.getOrder(orderId);
          String totalFee  =  (Math.round(orderList.get(0).getPayment().doubleValue()* 100 )) + "" ;
        // 获取登录用户名
        String userId = request.getRemoteUser();
        // 根据登录用户名，从Redis数据库获取支付日志对象
       PayLog payLog  =  payLogService.getpayLog(userId,orderId);

      //  PayLog payLog = orderService.findPayLogFromRedis(userId);
     String outTradeNo = payLog.getOutTradeNo();
        // 调用微信支付服务接口
        return weixinPayService.genPayCode("_"+outTradeNo, totalFee);
    }



    /** 检测支付状态 */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo,String orderId){
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try{
            // 调用微信支付服务接口
            Map<String, String> resMap = weixinPayService.queryPayStatus("_"+outTradeNo);
            // 判断支付状态
            if (resMap != null && resMap.size() > 0){
                if ("SUCCESS".equals(resMap.get("trade_state"))){ // 支付成功

                    // 支付成功(业务处理)
                    // 修改支付状态、订单状态、删除支付日志
                    orderService.updateStatusByUser(outTradeNo, resMap.get("transaction_id"),orderId);

                    data.put("status", 1);
                }
                if ("NOTPAY".equals(resMap.get("trade_state"))){ // 未支付
                    data.put("status", 2);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }
}
