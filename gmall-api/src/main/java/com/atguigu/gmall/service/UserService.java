package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    void addUser(String memberId, String username, String password, String nickname, String phone);

    void deleteUserById(String memberId);

    void updatePasswordByUserName(String username, String password);

    void updatePasswordByMemberId(String memberId, String password);
}
