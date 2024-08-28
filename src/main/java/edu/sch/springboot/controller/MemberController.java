package edu.sch.springboot.controller;


import edu.sch.springboot.domain.Member;
import edu.sch.springboot.dto.MemberDto;
import edu.sch.springboot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {
    //MemberService ms = new MemberService(); // tight coupling

    MemberService memberService;        // loose coupling
    // 생성자 DI
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService; // parameter 로 자동 주입
    }


    @GetMapping("/")        // http://127.0.0.1:8080/
    public String home() {
        return "home";      //resources/templates/home.html
    }

    @GetMapping("/members")
    public String members(Model model) {
        List<Member> list = memberService.findMembers();
        model.addAttribute("members", list);

        return "members/memberList";      //resources/templates/home.html
    }

    // 회원 가입 로직 처리 (Hash Map에 넣기 위함)
    @PostMapping("/members/signup")     // name = 홍길동
    public String signupProc(MemberDto memberDto) {
        Member member = new Member();
        member.setName(memberDto.getName());
        member.setAddress(memberDto.getAddress());

        memberService.join(member);

        return "redirect:/";
    }


    // 회원가입 화면
    @GetMapping("/members/signup")
    public String signup() {
        return "members/signupForm";      //resources/templates/home.html
    }

}
