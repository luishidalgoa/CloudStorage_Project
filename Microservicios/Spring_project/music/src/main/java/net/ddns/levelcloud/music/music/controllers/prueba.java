package net.ddns.levelcloud.music.music.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prueba")
public class prueba {
    @GetMapping("/")
    public String prueba() {
        return "Hola mundo";
    }
}
