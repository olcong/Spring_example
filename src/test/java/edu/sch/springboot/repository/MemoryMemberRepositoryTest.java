package edu.sch.springboot.repository;
import edu.sch.springboot.domain.Member;
import org.junit.jupiter.api.Test;


public class MemoryMemberRepositoryTest {
    MemoryMemberRepository memberDao = new MemoryMemberRepository();

    /**
     * save 메소드 테스트 케이스
     */
    @Test
    public void saveTest() {
        Member member = new Member();
        member.setName("spring");

        memberDao.save(member);

        Member result = memberDao.findById(member.getId()).get(); //store에 저장된 객체 가져오기 이름은 spring1이라는애 ㅇㅇ
        //System.out.println("result : " + (result == member));

//        org.junit.jupiter.api.Assertions.assertEquals(member, result);
//        이거 주석처리된 거 두개 다 비교하는 역할임 그래서 하나만 쓸거라 주석처리한거 ㅇㅇ
        org.assertj.core.api.Assertions.assertThat(member).isEqualTo(result);
    }
}