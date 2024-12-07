package com.elastic.elasticsearchdemo.web;

import com.auth0.jwt.JWT;
import com.elastic.elasticsearchdemo.bean.RegUserDTO;
import com.elastic.elasticsearchdemo.bean.TUser;
import com.elastic.elasticsearchdemo.bean.TUserDTO;
import com.elastic.elasticsearchdemo.bean.User;
import com.elastic.elasticsearchdemo.enums.ResponseEnum;
import com.elastic.elasticsearchdemo.response.ResponseBean;
import com.elastic.elasticsearchdemo.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class UserController {
    @Autowired
    private TUserService userService;
    @PostMapping("/register")
    public ResponseBean register(@RequestBody RegUserDTO regUser){
        userService.registerUser(regUser);
        return ResponseBean.success(regUser);
    }
    @PostMapping("login")
    public ResponseBean login(@RequestBody TUserDTO user){
        String token = userService.login(user);
        if (token!=null) {
            return ResponseBean.success(token);
        }
        return ResponseBean.fail(ResponseEnum.LOGIN_FAIL);
    }

}
