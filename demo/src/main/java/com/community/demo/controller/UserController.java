package com.community.demo.controller;

import com.community.demo.dto.UserDTO;
import com.community.demo.entity.User;
import com.community.demo.security.CustomAuthUser;
import com.community.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping("/join")
    public void join() {}

    @GetMapping("/login")
    public void login(HttpServletRequest request,
                      Model model){
        String email = (String)request.getSession().getAttribute("email");
        String errMsg = (String)request.getSession().getAttribute("errMsg");
        if(errMsg != null){
            log.info(">>> errMsg >> {}",errMsg);
            model.addAttribute("email", email);
            model.addAttribute("errMsg", errMsg);
        }
        request.getSession().removeAttribute("email");
        request.getSession().removeAttribute("errMsg");
    }

    @PostMapping("/join")
    public String join(UserDTO userDTO){
        String email = userService.register(userDTO);
        log.info(">>> email >> {}", email);
        return "redirect:/";
    }


    @GetMapping("/list")
    public String list(Model model) {
        log.info(">>> Admin User List Page 접속");

        // Service를 통해 데이터 확보 (Entity 리스트 반환)
        List<User> userList = userService.getList();

        model.addAttribute("userList", userList);
        return "user/list"; // templates/user/list.html
    }

    @GetMapping("password")
    public void password() {}

    @GetMapping("/charts")
    public void charts(){}

    @GetMapping("/modify")
    public String modify(@AuthenticationPrincipal CustomAuthUser customUser, Model model) {
        // 1. 로그인이 안 되어 있으면 customUser는 null입니다.
        if (customUser == null) {
            return "redirect:/user/login";
        }

        // 2. 로그는 찍히는지 확인 (터미널/콘솔 확인용)
        System.out.println("로그인 유저 정보: " + customUser.getUser());

        // 3. ★ 핵심: HTML에서 쓸 "user"라는 이름으로 데이터를 담아 보냄
        // 여기서 담는 객체는 entity.User 객체여야 합니다.
        model.addAttribute("user", customUser.getUser());

        return "user/modify";
    }

}
