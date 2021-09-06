package com.example.querydslstudy;

import com.example.querydslstudy.entity.Member;
import com.example.querydslstudy.entity.QMember;
import com.example.querydslstudy.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.querydslstudy.entity.QMember.*;
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

        //QMember m = new QMember("m"); //이건 실무에서 안쓴다. static import를 한다.

        Member findMember = queryFactory
                .select(member) //QMember를 static import를 통하여 사용한다.
                .from(member)
                .where(member.username.eq("member1")) //parameter binding이 필요없다. jdbc에 있는 preparestatment를 사용해서 자동으로 해줌.
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search () {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.between(10, 30)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam () {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1")
                        ,member.age.eq(10) //and를 ,로 끊어서 갈수 도있다. 여러개를 ,로 연결하면 디폴트로 and로 연결된다.
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch () {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch(); //list로 조회

        Member fetchOne = queryFactory
                .selectFrom(QMember.member)
                .fetchOne(); //단건조회 / 결과없으면 null. 2개이상이면 nonunique 예외 터짐

        Member fetchFirst = queryFactory
                .selectFrom(QMember.member)
                .fetchFirst();// .limit(1).fetchOne() 과 같다.


        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
        results.getTotal(); //paging을 하기위한 total count를 가져옴
        List<Member> content = results.getResults(); //content를 가져옴

        //count query만 가져오는 것임
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    // 정렬

    /**
     * 회원 정렬순서
     * 1. 회원 나이 내림차순 (desc)
     * 2. 회원 이름 올림차순 (asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력 (null last 기능)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100)) //위의 예제에서는 다 100살이라 이렇게하는것
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();

    }

}
