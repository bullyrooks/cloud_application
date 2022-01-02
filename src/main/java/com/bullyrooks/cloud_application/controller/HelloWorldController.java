package com.bullyrooks.cloud_application.controller;

import com.bullyrooks.cloud_application.controller.dto.HelloWorldResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/helloworld")
    public HelloWorldResponse getHelloWorld(){
        return HelloWorldResponse.builder().message("Hello World!").build();
    }

}
