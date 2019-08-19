package com.xl.miaosha.controller;

import com.xl.miaosha.result.Result;
import com.xl.miaosha.service.MiaoshaUserService;
import com.xl.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response,@Valid LoginVo loginVo){
        log.info("登录请求========="+loginVo);
//        遇到异常，直接抛出去，由全局异常处理器处理
        String token = miaoshaUserService.login(response, loginVo);
        return Result.success(token);
    }

}
