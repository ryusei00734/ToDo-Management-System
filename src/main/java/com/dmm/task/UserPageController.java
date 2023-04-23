package com.dmm.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {
	@GetMapping("/userPage")
	public String userPage() {
		return "userPage";
	}
}
