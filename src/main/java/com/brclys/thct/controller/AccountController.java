package com.brclys.thct.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @GetMapping("/sayHi/{userName}")
    @ResponseStatus(HttpStatus.OK)
    public String sayHi(@PathVariable("userName") String user) {

        return "Hey " + user;
    }
}
