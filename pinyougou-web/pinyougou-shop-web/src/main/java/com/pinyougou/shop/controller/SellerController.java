package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 商家控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-03-03<p>
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    // @Autowired spring容器中的bean
    @Reference(timeout = 10000)
    private SellerService sellerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    /** 商家入驻 */
    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller){
        try{
            // 密码加密
            String password = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(password);
            sellerService.save(seller);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 获取对应商家信息
     *
     * @param sellerId
     * @return
     */
    @GetMapping("/sellerInfo")
    public Seller sellerInfo(String sellerId) {
        try {
            return sellerService.findOne(sellerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存修改信息
     *
     * @param seller
     * @return
     */
    @PostMapping("/savesellerInfo")
    public boolean savesellerInfo(@RequestBody Seller seller) {
        try {
            sellerService.update(seller);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @PostMapping("/updatePassword")
    public boolean updatePassword(@RequestBody Seller seller, String oldPassword) {
        try {
            //创建用户名和密码对象
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(seller.getSellerId(), oldPassword);
            //登陆认证(Spring Security)
            Authentication authenticate = authenticationManager.authenticate(token);
            if (authenticate.isAuthenticated()) {//认证成功
                //给密码加密
                String password = passwordEncoder.encode(seller.getPassword());
                seller.setPassword(password);
                //更新密码
                sellerService.update(seller);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
