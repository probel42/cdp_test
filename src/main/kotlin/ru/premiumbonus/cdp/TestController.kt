package ru.premiumbonus.cdp

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/test")
class TestController {
    @GetMapping
    fun test(): String {
        return "OK"
    }
}