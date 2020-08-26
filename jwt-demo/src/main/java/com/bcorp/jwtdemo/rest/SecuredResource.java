package com.bcorp.jwtdemo.rest;

import com.bcorp.jwtdemo.security.AuthoritiesConstants;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SecuredResource {
    @GetMapping("/resource")
    @ResponseBody
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public String securedResource() {
        return "Aw shucks, you got me";
    }
}
