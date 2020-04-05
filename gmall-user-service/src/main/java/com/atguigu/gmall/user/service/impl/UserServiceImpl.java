package com.atguigu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;


    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMemberList = userMapper.selectAll();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
//        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
//        umsMemberReceiveAddress.setMemberId(memberId);
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsMemberReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddressList;
    }

    @Override
    public void addUser(String memberId, String username, String password, String nickname, String phone) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(memberId);
        umsMember.setUsername(username);
        umsMember.setPassword(password);
        umsMember.setNickname(nickname);
        umsMember.setPhone(phone);
        userMapper.insert(umsMember);
    }

    @Override
    public void deleteUserById(String memberId) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(memberId);
        userMapper.delete(umsMember);
    }

    @Override
    public void updatePasswordByMemberId(String memberId, String password) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(memberId);
        umsMember.setPassword(password);
        userMapper.updateByPrimaryKeySelective(umsMember);
    }

    @Override
    public void updatePasswordByUserName(String username, String password) {
        Example example = new Example(UmsMember.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        UmsMember umsMember = new UmsMember();
        umsMember.setPassword(password);
        userMapper.updateByExampleSelective(umsMember, example);
    }


}
