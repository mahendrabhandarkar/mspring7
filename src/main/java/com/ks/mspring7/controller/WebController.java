package com.ks.mspring7.controller;

import com.ks.mspring7.service.WebLoginService;
import com.ks.mspring7.user.User;
import com.ks.mspring7.user.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.*;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin("*")
@Controller
@RequestMapping("/web/v1")
@RequiredArgsConstructor
public class WebController {
    private final UserRepository repository;

    private final UserDetailsService userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    private HttpSession session;
    @Autowired
    public WebLoginService webLoginService;

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
    public String loginCheck(HttpServletRequest request, @RequestParam("email") String userEmail, @RequestParam("password") String userPass) {
        log.debug("Tester");
        log.debug(userEmail);
        log.debug(userPass);
        System.out.println("userEmail:" + userEmail );
        if(webLoginService.isValidLogin(userEmail, userPass, "") == true){
            session = request.getSession(true);
            // Store data in the session
            session.setAttribute("username", userEmail);
            return "redirect:/web/v1/welcome?login=success";
        }
        return "redirect:/web/v1/login?login=fail";
    }

    @GetMapping("/welcome")
 //   @preAuthorize("hasAnyRole('ROLE_ADMIN')")
 //   @preAuthorize("hasAuthority('employee:write')")
    public String welcome(){
    //    System.out.println("Session Username:" + session.getAttribute("username"));
    //    System.out.println("Currently Logged in User:" + SecurityContextHolder.getContext().getAuthentication().getName());
    //    System.out.println(SecurityContextHolder.getContext().toString());
        return "ks/index2";
    }

    @RequestMapping("/accessdenied")
    public ModelAndView accessdenied() {
        return new ModelAndView("/ks/index");
    }

}
