package edu.sch.springboot.service;

import edu.sch.springboot.repository.MemoryMemberRepository;
import edu.sch.springboot.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public MemberService memberService() {
        return new MemberService(memberDao());
    }

    @Bean
    public MemoryMemberRepository memberDao() {
        return new MemoryMemberRepository();
    }
}
