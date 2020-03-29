package com.atguigu.gmall.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("index")
    @ResponseBody
    public String index(){
        return "hello user";
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser(){
        List<UmsMember> umsMemberList = userService.getAllUser();
        return umsMemberList;
    }

    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = userService.getReceiveAddressByMemberId(memberId);
        return umsMemberReceiveAddressList;
    }

    //增加用户memberId、username、password、nickname、phone
    @RequestMapping("addUser")
    @ResponseBody
    public String addUser(String memberId,String username,String password,String nickname,String phone){
        userService.addUser(memberId,username,password,nickname,phone);
        return "add success";
    }

    //删除用户 deleteUserById
    @RequestMapping("deleteUserById")
    @ResponseBody
    public String deleteUserById(String memberId){
        userService.deleteUserById(memberId);
        return "delete success";
    }

    //修改用户密码 updatePasswordByMemberId
    @RequestMapping("updatePasswordByMemberId")
    @ResponseBody
    public String updatePasswordByMemberId(String memberId,String password){
        userService.updatePasswordByMemberId(memberId,password);
        return "updatePasswordByMemberId success";
    }

    //修改用户密码 updatePasswordByUserName
    @RequestMapping("updatePasswordByUserName")
    @ResponseBody
    public String updatePasswordByUserName(String username,String password){
        userService.updatePasswordByUserName(username,password);
        return "updatePasswordByUserName success";
    }
}
