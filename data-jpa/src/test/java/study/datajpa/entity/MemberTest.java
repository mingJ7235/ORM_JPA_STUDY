package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback (false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity (){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        Member member3 = new Member("member3", 10, teamB);
        Member member4 = new Member("member4", 10, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush();
        em.clear();

        //확인
        List<Member> select_m_from_member_m = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : select_m_from_member_m) {
            System.out.println("member =" +member);
            System.out.println("team = " + member.getTeamss()) ;
        }
    }

    @Test
    public void JpaEventBaseEntity () throws InterruptedException {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist가 발생함 (JpaBaseEntity에 있는)

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); //@PreUpdate가 발생
        em.clear();
        //when

        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
        System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate()); //100ms 후에 발동이됨
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());


    }
}

