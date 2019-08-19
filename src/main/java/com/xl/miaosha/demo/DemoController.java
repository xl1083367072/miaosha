package com.xl.miaosha.demo;

import com.xl.miaosha.domain.User;
import com.xl.miaosha.redis.RedisService;
import com.xl.miaosha.redis.UserKey;
import com.xl.miaosha.result.CodeMsg;
import com.xl.miaosha.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    RedisService redisService;

    @RequestMapping("/success")
    public Result<String> success(){
        return Result.success("success");
    }

    @RequestMapping("/error")
    public Result<String> error(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/get")
    public Result<User> get(){
        User user = redisService.get(UserKey.getById,"1", User.class);
        return Result.success(user);
    }

    @RequestMapping("/set")
    public Result<Boolean> set(){
        User user = new User();
        user.setId(1);
        user.setName("测试");
        boolean res = redisService.set(UserKey.getById,"1", user);
        return Result.success(res);
    }

}
