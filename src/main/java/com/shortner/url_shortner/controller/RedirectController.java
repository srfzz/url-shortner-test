package com.shortner.url_shortner.controller;

import com.shortner.url_shortner.services.RedirectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirectController {
    private final RedirectService redirectService;
    public RedirectController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }
    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirecto(@PathVariable String shortUrl, HttpServletRequest req, HttpServletResponse res){

      String  orignalUrl=  redirectService.fetchUrlAndRedirect(shortUrl,req,res);
      return  ResponseEntity.status(HttpStatus.FOUND).location(URI.create(orignalUrl)).build();

    }
}
