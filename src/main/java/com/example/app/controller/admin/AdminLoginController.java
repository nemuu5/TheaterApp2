package com.example.app.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.domain.Admin;
import com.example.app.domain.Login;
import com.example.app.login.LoginAuthority;
import com.example.app.login.LoginStatus;
import com.example.app.service.AdminService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminLoginController {

	private final AdminService service;
	private final HttpSession session;

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute(new Login());
		return "admin/login-admin";
	}

	@PostMapping("/login")
	public String login(
			@Valid Login login,
			Errors errors) throws Exception {
		if(errors.hasErrors()) {
			return "admin/login-admin";
		}

		Admin admin = service.getAdminByLoginId(login.getLoginId());
		if(admin == null || !login.isCorrectPassword(admin.getLoginPass())) {
			errors.rejectValue("loginId", "error.incorrect_id_or_password");
			return "admin/login-admin";
		}

		// セッションに認証情報を格納
		LoginStatus loginStatus = LoginStatus.builder()
				.id(admin.getId())
				.name(admin.getName())
				.loginId(admin.getLoginId())
				.authority(LoginAuthority.ADMIN)
				.build();
		session.setAttribute("loginStatus", loginStatus);
		return "redirect:/admin/material/list";
	}

	@GetMapping("/logout")
	public String logout(
			RedirectAttributes redirectAttributes) {
		session.removeAttribute("loginStatus");
		redirectAttributes.addFlashAttribute("message", "ログアウトしました。");
		return "redirect:/admin/login";
	}
}
