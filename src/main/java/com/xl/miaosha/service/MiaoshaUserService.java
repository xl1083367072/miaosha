package com.xl.miaosha.service;

import com.xl.miaosha.dao.MiaoshaUserDao;
import com.xl.miaosha.domain.MiaoshaUser;
import com.xl.miaosha.exception.GlobalException;
import com.xl.miaosha.redis.MiaoshaUserKey;
import com.xl.miaosha.redis.RedisService;
import com.xl.miaosha.result.CodeMsg;
import com.xl.miaosha.utils.Md5Util;
import com.xl.miaosha.utils.UUIDUtil;
import com.xl.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_TOKEN_NAME = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(String id){
        MiaoshaUser user = redisService.get(MiaoshaUserKey.id, "" + id, MiaoshaUser.class);
        if(user!=null){
            return user;
        }
//        对象级缓存
        user = miaoshaUserDao.getById(Long.parseLong(id));
        if(user!=null){
            redisService.set(MiaoshaUserKey.id, "" + id,user);
        }
        return user;
    }

    public boolean updatePassword(String token,long id,String newPassword){
        MiaoshaUser user = getById(String.valueOf(id));
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXISTS);
        }
        user = new MiaoshaUser();
        user.setId(id);
        user.setPassword(newPassword);
        miaoshaUserDao.updatePassword(user);
        redisService.del(MiaoshaUserKey.token,token);
        user.setPassword(user.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, "-" + token, MiaoshaUser.class);
        if(user!=null){
            addCookie(response,user,token);
        }
        return user;
    }

    public String login(HttpServletResponse response,LoginVo loginVo){
        if(loginVo==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPwd = loginVo.getPassword();
        MiaoshaUser user = getById(mobile);
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXISTS);
        }
        String dbPwd = Md5Util.form2DB(formPwd, user.getSalt());
        if(!user.getPassword().equals(dbPwd)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUtil.random();
        addCookie(response,user,token);
        return token;
    }

    private void addCookie(HttpServletResponse response,MiaoshaUser user, String token){
        redisService.set(MiaoshaUserKey.token,"-"+token,user);
        Cookie cookie = new Cookie(COOKIE_TOKEN_NAME,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }



}
