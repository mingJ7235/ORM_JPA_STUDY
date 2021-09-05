package com.example.querydslstudy;

import com.example.querydslstudy.entity.Member;
import com.example.querydslstudy.entity.QMember;
import com.example.querydslstudy.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory; //필드레벨로 가지고와서 이렇게 처리해도된다.

    @BeforeEach  //data를 미리 넣어두기 위해 세팅
    public void before () {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //entitymanager를 넘겨야함
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL (){
        //member1 을 찾아라.

        String qlString = "select m from Member m " +
                "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * JPQL이나 native Query는 쿼리들이 String이기 때문에 실제 오류를 발생하는 시점은 실행이되고나서 알수있다.
     * 즉, Runtime오류를 발생시킨다.
     * 하지만 QueryDSL은, 오타나 오류가 난다면 compile 오류가 난다.
     * 쿼리를 java문법으로 작성하는 것이다. 너무 좋은점!!
     * query를 java문법으로 사용할 수 있고,
     * parameter를 자동으로해주며
     * code assistance의 도움을 받을 수 있다.
     * 언어의 한계를 돌파해준 시도가 성공한것이다.
     */
    @Test
    public void startQuerydsl () {

        QMember m = new QMember("m"); //이건 실무에서 안쓴다. Qmember.member를 사용함

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1")) //parameter binding이 필요없다. jdbc에 있는 preparestatment를 사용해서 자동으로 해줌.
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

}
