package edu.sch.springboot.controller;

import edu.sch.springboot.domain.Member;
import edu.sch.springboot.dto.MemberDto;
import edu.sch.springboot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // React 개발 서버 주소
public class RestMemberController {
    //서비스 객체 생성 - 생성자
    private final MemberService memberService;

    @Autowired
    public RestMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 회원 등록
     */
    @PostMapping("/members/signup")
    public String signup(@RequestBody MemberDto memberDto) {
        System.out.println(memberDto.getName());
        Member member = new Member();
        member.setName(memberDto.getName());
        //Member 객체 생성
        //name 추가
        //Service join
        memberService.join(member);

        return "ok";
    }

    /**
     * 회원 리스트 조회
     */
    @GetMapping("/members")
    public ResponseEntity<List<Member>> list(Model model) {
        List<Member> members = memberService.findMembers();
        return ResponseEntity.ok(members);
    }
}
