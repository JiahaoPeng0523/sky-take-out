package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String GRANT_TYPE = "authorization_code";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;


    @Override
    public User userLogin(UserLoginDTO userLoginDTO) {
        // 根据微信用户code获取openId
        String openid = getOpenId(userLoginDTO.getCode());
        // 判断openId是否为空，若为空则抛出异常
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断是否为新用户，若是则在user表中自动完成注册
        User user = this.lambdaQuery()
                .eq(User::getOpenid, openid)
                .one();
        if(user==null){
            User newUser = new User().builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            this.save(newUser);
        }
        // 返回这个用户对象
        user = this.lambdaQuery()
                .eq(User::getOpenid, openid)
                .one();
        return user;
    }

    private String getOpenId(String code){
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", GRANT_TYPE);
        String json = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", map);
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }
}
