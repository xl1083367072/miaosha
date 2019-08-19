package com.xl.miaosha.inteceptor;

import com.alibaba.fastjson.JSON;
import com.xl.miaosha.config.AccessLimit;
import com.xl.miaosha.config.UserThreadLocal;
import com.xl.miaosha.domain.MiaoshaUser;
import com.xl.miaosha.redis.MiaoshaKey;
import com.xl.miaosha.redis.RedisService;
import com.xl.miaosha.result.CodeMsg;
import com.xl.miaosha.result.Result;
import com.xl.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessLimitInteceptor extends HandlerInterceptorAdapter {

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            MiaoshaUser user = getByToken(request, response);
            UserThreadLocal.setUser(user);
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit==null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String limitKey = request.getRequestURI();
            if(needLogin){
                if(user==null){
                    returnMsg(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                limitKey += user.getId();
            }
            MiaoshaKey access = MiaoshaKey.getLimitKey(seconds);
            Integer count = redisService.get(access, limitKey, Integer.class);
            if(count==null){
                redisService.set(access,limitKey,1);
            }else if(count<maxCount) {
                redisService.incr(access,limitKey);
            }else {
                returnMsg(response,CodeMsg.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }

    //    根据token去redis中取用户信息
    private MiaoshaUser getByToken(HttpServletRequest request,HttpServletResponse response){
        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_TOKEN_NAME);
        String cookieToken = getCookieValue(request,MiaoshaUserService.COOKIE_TOKEN_NAME);
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return miaoshaUserService.getByToken(response,token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName){
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<=0){
            return null;
        }
        for (Cookie cookie:cookies){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }

    //    拦截器返回false时，向客户端返回错误信息
    private void returnMsg(HttpServletResponse response, CodeMsg codeMsg){
        response.setContentType("application/json;charset=UTF-8");
        try (OutputStream os = response.getOutputStream()){
            String result = JSON.toJSONString(Result.error(codeMsg));
            os.write(result.getBytes("UTF-8"));
            os.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
