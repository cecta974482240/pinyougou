package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.pojo.User;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.UserService;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-03-16<p>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;
    @Reference(timeout = 10000)
    private AddressService addressService;

    /** 用户注册 */
    @PostMapping("/save")
    public boolean save(@RequestBody User user, String code){
        try{
            // 检验短信验证码
            boolean flag = userService.checkSmsCode(user.getPhone(), code);
            if (flag) {
                userService.save(user);
            }
            return flag;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 发送短信验证码 */
    @GetMapping("/sendSmsCode")
    public boolean sendSmsCode(String phone){
        try{
            return userService.sendSmsCode(phone);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**查询用户信息 */
    @GetMapping("/showMessage")
    public User showMessage(HttpServletRequest request){
        String userId = request.getRemoteUser();
        return userService.findUserId(userId);
    }

    /** 保存用户信息 */
    @PostMapping("/updateMessage")
    public boolean updateMessage(@RequestBody User user,HttpServletRequest request){
        try {
            String userName = request.getRemoteUser();
            user.setUsername(userName);
            userService.update(user);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /** 查询省份信息 */
    @GetMapping("/findProvinceid")
    public List<Provinces> findProvinceid(){
        try{
            /** 查询所有省份 */
            return  addressService.findProvinceid();
        }catch (Exception e){
           e.printStackTrace();
        }
        return null;
    }

    /** 查询城市信息 */
    @GetMapping("/findCityById")
    public List<Cities> findCityById(Long provinceId){
        try{
            /** 查询所有省份 */
            return  addressService.findCityById(provinceId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /** 查询城区信息 */
    @GetMapping("/findAreaById")
    public List<Areas> findAreaById(Long cityId){
        try{
            /** 查询所有城区 */
            return  addressService.findAreaById(cityId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
