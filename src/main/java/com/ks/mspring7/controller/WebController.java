package com.ks.mspring7.controller;

import com.ks.mspring7.user.User;
import com.ks.mspring7.user.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@Controller
@RequestMapping("/web/v1")
@RequiredArgsConstructor
public class WebController {
    private final UserRepository repository;

    @GetMapping
    public String get() {
        return "ks/index";
    }
    @PostMapping
    @Hidden
    public String post() {
        return "POST:: web controller";
    }
    @PutMapping
    @Hidden
    public String put() {
        return "PUT:: web controller";
    }
    @DeleteMapping
    @Hidden
    public String delete() {
        return "DELETE:: web controller";
    }

    @GetMapping("/login")
    public String login() { return "ks/login"; }

    @PostMapping("/loginCheck")
    public String loginCheck(@PathVariable("email") String userEmail, @RequestParam("password") String userPass) {

        User user = repository.findByEmail(userEmail).orElseThrow();
        System.out.println("Tester");
        System.out.println(userEmail);
        System.out.println(userPass);
//        System.exit(0);
        return "redirect:/web/v1/welcome";
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "/ks/index";
    }
}
