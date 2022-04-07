package com.ssafy.nooni.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BootstrapController {
	
	@RequestMapping("/dashboard")
	public String dashboard() {
		return "index";
	}
}
