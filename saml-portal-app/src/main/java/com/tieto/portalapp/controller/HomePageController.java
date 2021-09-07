package com.tieto.portalapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomePageController {
    public static final String WELCOME_PAGE = "/welcome";

    private static final Logger LOGGER = LoggerFactory.getLogger(HomePageController.class);

    @RequestMapping(value = "/")
    public String welcome(){
        LOGGER.info("Rendering the welcome page");
        return WELCOME_PAGE;
    }
}
