package edu.sch.springboot.service;

import edu.sch.springboot.domain.Member;
import edu.sch.springboot.repository.MemberRepository;
import edu.sch.springboot.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class MemberService {
    private final MemberRepository memberDao;

    @Autowired
    public MemberService(MemberRepository memberDao) {
        this.memberDao = memberDao;
    }
    /**
     * 회원 가입
     * */
    public Long join(Member member) {
        duplicatedMemberCheck(member);

        memberDao.save(member);
        return member.getId();
    }

    //회원 name 중복 체크
    private void duplicatedMemberCheck(Member member) {
        memberDao.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberDao.findAll();
    }


    /**
     * 회원 ID 조회
     **/
    public Optional<Member> findMemberId(Long memberId) {
        return memberDao.findById(memberId);
    }

}

