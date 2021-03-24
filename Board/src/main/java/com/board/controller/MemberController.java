
package com.board.controller;

import com.board.domain.BoardDTO;
import com.board.domain.Member;
import com.board.Dto.MemberDto;
import com.board.service.MemberService;
import com.board.util.UiUtils;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import javax.validation.Valid;

@Controller

@AllArgsConstructor
public class MemberController extends UiUtils {
	private MemberService memberService;



	@GetMapping("/admin/memberlist")
	public String memberlist(Model model) {
		try {
			return "admin/memberlist";
		}
		catch(Exception e){
			model.addAttribute("msg", "권한이 없습니다.");
			model.addAttribute("url", "/");
			return "utils/message";
		}
	}

	@GetMapping("member/signup")
	public String signupForm(Model model) {
		model.addAttribute("member", new MemberDto());
		return "member/signupForm";
	}

	@PostMapping("member/signup")
	public String signup(MemberDto memberDto, Model model) {
		model.addAttribute("msg", "이미 가입된 이메일입니다.");
		model.addAttribute("url", "login");
		try {
			memberService.signUp(memberDto);
		} catch (Exception e) {
			return "utils/message";
		}

		return "member/loginForm";
	}
	

    @GetMapping("member/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "exception", required = false) String exception,
            Model model) {
    	model.addAttribute("error",error);
        model.addAttribute("exception",exception);
        return "member/loginForm";
    }

}
