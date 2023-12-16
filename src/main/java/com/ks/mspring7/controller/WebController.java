package com.ks.mspring7.controller;

import com.ks.mspring7.user.User;
import com.ks.mspring7.user.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.*;

@CrossOrigin("*")
@Controller
@RequestMapping("/web/v1")
@RequiredArgsConstructor
public class WebController {
    private final UserRepository repository;

    private final UserDetailsService userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);


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
    public String loginCheck(@RequestParam("email") String userEmail, @RequestParam("password") String userPass) {
        log.debug("Tester");
        log.debug(userEmail);
        log.debug(userPass);
/*
        User user = repository.findByEmail(userEmail).orElseThrow();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        String validatePass = this.passwordEncoder.encode(userPass);


        if(userDetails.getPassword().equals(validatePass)) {
            return "redirect:/web/v1/welcome";
        }
//        System.exit(0);
 */
        return "redirect:/web/v1/login";
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "/ks/index";
    }
}
