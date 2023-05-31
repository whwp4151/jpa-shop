package jpabook.jpashop;

import java.util.Optional;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Member save = memberRepository.save(member);
        Optional<Member> memberOptional = memberRepository.findById(save.getId());

        // then
        Assertions.assertTrue(memberOptional.isPresent());
        Assertions.assertEquals(memberOptional.get().getId(), member.getId());
        Assertions.assertEquals(memberOptional.get().getUsername(), member.getUsername());
        Assertions.assertEquals(memberOptional.get(), member);
        System.out.println("findMember == member : " + (memberOptional.get() == member));
    }

}

