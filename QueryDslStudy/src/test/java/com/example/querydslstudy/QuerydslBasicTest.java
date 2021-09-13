package com.example.querydslstudy;

import com.example.querydslstudy.dto.MemberDto;
import com.example.querydslstudy.dto.QMemberDto;
import com.example.querydslstudy.dto.UserDto;
import com.example.querydslstudy.entity.Member;
import com.example.querydslstudy.entity.QMember;
import com.example.querydslstudy.entity.QTeam;
import com.example.querydslstudy.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.querydslstudy.entity.QMember.member;
import static com.example.querydslstudy.entity.QTeam.team;
import static com.querydsl.core.types.Projections.*;
import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory; //필드레벨로 가지고와서 이렇게 처리해도된다.

    @BeforeEach  //data를 미리 넣어두기 위해 세팅
    public void before () {
        queryFactory = new JPAQueryFactory(em); //entitymanager를 넘겨야함
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

//        Member fetchOne = queryFactory
//                .selectFrom(member)
//                .fetchOne(); //단건조회 / 결과없으면 null. 2개이상이면 nonunique 예외 터짐

        Member fetchFirst = queryFactory
                .selectFrom(member)
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
     * 단 회원 이름이 없으면 마지막에 출력 (null last 기능)
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

    @Test
    public void paging1 () {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2 () {
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);

    }

    @Test
    public void aggregation () throws Exception{

        List<Tuple> result = queryFactory
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);

    }

    /**
     * 팀의 이름과 각 팀의 평균 연력을 구해라.
     */

    @Test
    public void groupTest () {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple.get(team.name) = " + tuple.get(team.name));
            System.out.println("tuple.get(member.age) = " + tuple.get(member.age));
        }

        System.out.println(result.get(0));
        System.out.println(result.get(1));

    }


    @Test
    public void group () throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();
        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat((teamA.get(member.age.avg()))).isEqualTo(15);
    }

    /**
     * teamA에 소속된 모든 회원을 찾아라
     */
    @Test
    public void join () {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team) //leftjoin,
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result) //username 이 member1, member2 인지 검증하는 방법
                .extracting("username") //username 필드
                .containsExactly("member1", "member2"); //result 검색결과의 username 필드가 member1, member2인지 검증
    }

    /**
     *
     * 연관관계없어도 다 조회하는 세타조인
     * JPA도 연관관계가 없을 지라도 조인이 가능하다.
     *
     * from 절에 여러 엔티티를 선택하여 세타조인이 가능하다.
     * but outer 조인이 불가능했다.(left join이안됫음) -> 조인 on을 사용하면 outer 조인도 가능하다.(hibernate 업그레이드)
     *
     * 억지성 예제
     * 회원의 이름이 팀 이름과 같은 회원을 조회... -> 약간 억지가있지만
     */
    @Test
    public void theta_join () {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team) //세타조인은 그냥 바로 이렇게 엔터티 나열. 위의 join과 다름
                .where(member.username.eq(team.name))
                .fetch();
        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * join on 절
     * - join 대상을 필터링
     * - 연관관계 없는 엔티티 외부 조인 (세타조인)
     */
    /**
     * 1. join 대상을 필터링
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL : select m, t from Member m left join m.team t on t.name = 'teamA'
     */
    @Test
    public void join_on_filtering() {
        List<Tuple> result = queryFactory //tuple로 나온 이유는, select 타입이 member와 team이기때문이다.
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA")) //on을 사용하여 한번 더 필터링 (outer join은 where가 먹지 않는다.)
                    //.join(member.team, team)
                    //.on(team.name.eq("teamA"))
                    //.where(team.name.eq("teamA")) //innerjoin 에서 on은 where와 같은 결과를 가져온다. //보통은 where를 많이 사용한다.
                                                    //but, outer join에서는 on만 기능한다. where는 기능안한다.
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
        /** iter 결과물
         * tuple = [Member(id=3, username=member1, age=10), Team(id=1, name=teamA)]
         * tuple = [Member(id=4, username=member2, age=20), Team(id=1, name=teamA)]
         * tuple = [Member(id=5, username=member3, age=30), null]
         * tuple = [Member(id=6, username=member4, age=40), null]
         */
    }

    /**
     * 2. 연관관계가 없는 엔티티 외부 조인 (세타조인)
     * 예) 회원의 이름이 팀 이름과 같은 대상 외부 조인
     */

    @Test
    public void join_on_no_relation () {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                //막조인이기때문에 member.team, team 이 아니라 그냥 team
                //member.team, team 을 넣으면 id로 서로 조인이 되어서 가져오게됨
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
        /** iter 결과
         * tuple = [Member(id=3, username=member1, age=10), null]
         * tuple = [Member(id=4, username=member2, age=20), null]
         * tuple = [Member(id=5, username=member3, age=30), null]
         * tuple = [Member(id=6, username=member4, age=40), null]
         * tuple = [Member(id=7, username=teamA, age=0), Team(id=1, name=teamA)]
         * tuple = [Member(id=8, username=teamB, age=0), Team(id=2, name=teamB)]
         * tuple = [Member(id=9, username=teamC, age=0), null]
         */

//        assertThat(result)
//                .extracting("username")
//                .containsExactly("teamA", "teamB");
    }
    /**
     * fetch join
     * - sql에서 제공하는 기능이아니다.
     * - sql 조인을 활용하여 연관된 entity를 sql 한번에 조회하는 기능이다.
     * - 주로 성능 최적화에 사용하는 방법이다.
     */

    // fetch join 미적용
    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void noFetchJoin () {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        //lazy 조회기때문에 team은 조회가안된다.

        //증명하기 위해 -> emf를 사용하여 로드가 되었는지 여부를 조회해보면된다.
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("fetch join 미적용").isFalse(); // team이 로드되지 않았다면 false이고, 테스트를 통과하게 된다.
    }

    @Test
    public void useFetchJoin () {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin() //fetch join을 넣는 법
                .where(member.username.eq("member1"))
                .fetchOne();
        // -> 연관된 모든 것을 다 가져온다.

        //증명하기 위해 -> emf를 사용하여 로드가 되었는지 여부를 조회해보면된다.
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("fetch join 미적용").isTrue(); // team이 로드되지 않았다면 false이고, 테스트를 통과하게 된다.
    }

    @Test
    public void useFetchJoinReview () {
        em.flush();
        em.clear();

        Member result = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(result.getTeam());
        assertThat(loaded).as("fetch join 적용").isTrue();
    }

    /**
     * Sub Query
     * - com.querydsl.jpa.JPAExpressions를 사용한다.
     *
     */
    /**
     * 나이가 가장 많은 회원 조회
      */
    @Test
    public void subQuery () {

        QMember memberSub = new QMember("memberSub");// 서브쿼리에 들어갈 member는 겹치면안되므로 하나를 생성해줘야한다.

        List<Member> result = queryFactory
                .selectFrom(member) //member중에서
                .where(member.age.eq( //age 가 같은데, 뭐랑 같냐?
                        select(memberSub.age.max()) // subQuery로 넣어서, age가 가장 많은 것과 비교!
                                .from(memberSub) //서브쿼리의 엘리아스에서!
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(40);


    }
    /**
     * 나이가 가장 적은 회원 조회
     */
    
    @Test
    public void subQueryPractice () {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.min())
                                .from(memberSub)
                ))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1.getAge() = " + member1.getAge());
        }


    }

    /**
     * 나이가 평균 이상인 회원
     */
    @Test
    public void subQueryGoe () {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(30, 40);
    }

    /**
     * sub쿼리에서 in이 많이 사용된다.
     *
     */

    @Test
    public void subQueryIn () {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);
    }
    
    @Test
    public void subQueryInTest () {
        QMember memberSub = new QMember("memberSub");
        QTeam teamSub = new QTeam("teamSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.goe(20)
                                        .and(memberSub.team.name.eq(
                                                select(teamSub.name)
                                                        .from(teamSub)
                                                        .where(teamSub.name.eq("teamA"))
                                        ))
                                )
                ))
                .fetch();

        for (Member resultMember : result) {
            System.out.println("resultMember = " + resultMember);
        }

        assertThat(result.size()).isEqualTo(1);
    }

    /**
     * select 절에서 subquery 사용
     */

    @Test
    public void selectSubquery () {
        QMember memberSub = new QMember("memberSub");
        List<Tuple> result = queryFactory
                .select(member.username,

                        select(memberSub.age.avg()) //JPAExpressions를 static import했다.
                                .from(memberSub)
                )
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    /**
     * JPA, JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리는 지원하지 않는다.
     * 즉, Querydsl도 지원하지 않는다.
     *
     * from 절의 서브쿼리 해결 방안
     * - 서브쿼리를 join으로 변경한다. (안되는 경우도 있다.)
     * - 쿼리를 2번 분리해서 실행한다.
     * - nativeSQL을 사용한다.
     */

    /**
     * case 문
     * - select, 조건절 (where) 에서 사용가능
     */

    @Test
    public void basicCase () {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void complesCase () {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(20, 30)).then("21살~30살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * Querydsl로 상수, 문자 더하기
     */

    @Test
    public void constant () {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void constantPractic () {

        LocalDateTime now = LocalDateTime.now();
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.age,
                        Expressions.asDateTime(now))
                .from(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void concat () {
        //{usernmae}_{age} 를하려고하는 것
        List<String> result = queryFactory
                                                                //age는 int이므로 stringValue로 형변환해야함
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }

        /**
         * *참고
         * member.age.stringValue() 이부분이 중요하다.
         * 문자가 아닌 다른 타입들은 stringValue()로 문자로 변환할 수 있다. 이 방법은 ENUM을 처리할 때도 자주 사용한다.
         */
    }

    @Test
    public void concatStrTest () {

        QMember memberSub = new QMember("memberSub");

        List<String> result = queryFactory
                .select(member.username.concat("_by_")
                        .concat(member.team.name)
                        .concat("_age_")
                        .concat(member.age.stringValue()))
                .from(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ).and(member.team.name.eq("teamB")))
                .fetch();

        for (String concatS : result) {
            System.out.println("concatS = " + concatS);
        }

        System.out.println("result size : " + result.size());
        assertThat(result.size()).isEqualTo(0);
    }

    /**
     * Projection
     *
     * - projection 이란 select 대상을 지정하는 것을 말한다.
     *
     * - 프로젝션 대상이 하나라면 반환타입을 명확하게 지정할 수 있다.
     * - 둘 이상이면 tuple 이나 DTO로 조회
     */

    // projection 이 하나인경우
    @Test
    public void simpleProjection () {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }

        List<Member> fetch = queryFactory
                .select(member)
                .from(member)
                .fetch();

        for (Member fetch1 : fetch) {
            System.out.println("fetch1 = " + fetch1);
        }
    }
    //tuple로 projection을 받을 경우
    @Test
    public void tupleProjection() {
        List<Tuple> result = queryFactory
                .select(member.username, member.age) //타입이 다름
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
        /**
         * tuple 은 querydsl 패키지안에 있는것이므로
         * service 계층이나 로직에서 사용하면 좋지않다.
         * repository 계층에서 사용하고 다른곳에서 의존이 없도록 하는 것이 좋다.
         * 그렇기때문에, 바깥 계층으로 보낼때는 DTO로 묶어서 보내는 것이 좋다.
         */
    }

    @Test
    public void tupleMultiProjectionTest () {

        int cnt = 0;
        QMember memberSub = new QMember("memberSub");

        List<Tuple> tupleResult = queryFactory
                .select(member.username.concat("_team:")
                        .concat(member.team.name), member.age)
                .from(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        for (Tuple tuple : tupleResult) {
            cnt += 1;
            System.out.println("tuple" + cnt + " = " + tuple);
        }
    }
    /**
     * Projection 과 결과반환 DTO
     *
     */
    // DTO 조회 - JPQL로 할경우 new operation 을 사용해야한다.
    // package 명을 다 적어야한다. 이게 진짜 별로임. DTO의 package이름을 다 적어줘야하며
    // 생성자 방식만 지원한다.
    @Test
    public void findDtoByJpql () {
        List<MemberDto> resultList = em.createQuery("select new com.example.querydslstudy.dto.MemberDto(m.username, m.age) " +
                        "from Member m", MemberDto.class)
                .getResultList();
        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }
    /**
     * Querydsl 빈 생성 (Bean population)
     */

    // 1. setter 로 가져오는 방법 property 접근 방법
    // DTO는 기본 생성자, setter 가 있어야한다.
    @Test
    public void findDtoBySetterQueryDSL () {
        List<MemberDto> result = queryFactory
                .select(bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
    @Test
    public void newFindDtoBySetterQueryDSL () {
        List<MemberDto> result = queryFactory
                .select(bean(
                        MemberDto.class,
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    //2 . field로 하는 방법
    //getter setter 없어도 된다.
    // field에 꽂아준다.
    @Test
    public void findDtoByFieldQueryDSL () {
        List<MemberDto> result = queryFactory
                .select(fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test // fields로 넣을 때에는 주의점 : dto와 entity의 필드명이 같지않으면 as를 사용하여 적용시켜줘야한다.
    public void newFindDtoByFieldQueryDsl () {
        List<UserDto> result = queryFactory
                .select(fields(UserDto.class,
                        member.username.as("name"),
                        member.age
                ))
                .from(member)
                .fetch();
        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    //3. 생성자 접근 방법
    @Test
    public void findDtoByConstructorQueryDSL () {
        List<MemberDto> result = queryFactory
                .select(constructor(MemberDto.class,
                        member.username,
                        member.age)) //생성자 접근시 유의사항은, DTO클래스에 있는 생성자의 파라미터 타입들의 순서를 맞춰서 넣어야한다.
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void newFindDtoConstructorQueryDsl() {
        List<MemberDto> result = queryFactory
                .select(constructor(MemberDto.class, //생성자로 접근할때는 생성자의 파라미터 순서를 맞춰야한다.
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();
    }

    // DTO의 필드명이 다를경우, 필드명에 서브쿼리를 넣고싶은경우
    @Test
    public void findUserDtoByFieldQueryDSL () {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(fields(UserDto.class,
                        member.username.as("name"), //userDto 의 핆드명이 name이므로 이렇게 해줘야 인식한다. dto의 이름이 다를때는 이렇게 넣어주면된다.
                        /*member.age*/
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub), "age" // age 부분을 subquery로 만든것. alias를 줘야한다.
                        )))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    public void findUserDtoByFieldAndSubquerybyQuerydsl () {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(fields(
                        UserDto.class,
                        member.username.as("name"),
                        ExpressionUtils.as(
                                select(memberSub.age.max())
                                        .from(memberSub), "age" //alias를 꼭 써줘야한다.
                        )
                ))
                .from(member)
                .fetch();
    }

    //생성자 주입시 필드명이 다를 때
    @Test
    public void findUserDtoByConstructorQueryDSL () {
        List<UserDto> result = queryFactory
                .select(constructor(UserDto.class,
                        member.username, //constructor 주입은, field명으로 가지않고 타입으로 가기때문에 위와달리 dto와 entity의 필드명이 달라도 상관없다.
                        member.age))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    /**
     * 프로젝션과 결과 반됨
     */

    @Test
    public void findDtoByQueryProjection () {
        List<MemberDto> result = queryFactory
                //dto 클래스에 @queryprojection
                .select(new QMemberDto(member.username, member.age)) // constructor 의 순서로.
                                //이것의 장점은 constructor에 파라미터가 다른다면, 컴파일오류가나서 쉽게 에러를 잡아낼 수 있다.
                                //but, DTO 클래스가 QueryDSL에 의존성을 가지게 된다. 아키텍츠젹인 고민을 해야한다.
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 동적쿼리 - BooleanBuilder 사용
     *
     * 동적 쿼리를 해결하는 두가지 방식
     * - BooleanBuilder
     * - Where 다중 파라미터 사용
     */
    //BooleanBuilder
    @Test
    public void dynamicQuery_BooleanBuilder () {
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        //BooleanBuilder에서 조립
        BooleanBuilder builder = new BooleanBuilder(/*member.username.eq(usernameCond)*/); //생성자 파라미터에는 무조건 들어가야하는 조건 즉, not null 조건을 넣는것임!
        if(usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }

        if(ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();

        return result;
    }

    //Where 다중 파라미터 사용
    @Test
    public void dynamicQuery_whereParam () {
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);

    }

    //쿼리가 바로 직관적으로 파악이되어 유지보수시 편리하다.
    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameCond), ageEq(ageCond)) //where 파라미터에 null 이들어오면 아무동작하지 않는다. 이렇게 되기때문에, 동적쿼리가 완성된다.
                //.where(allEq(usernameCond, ageCond)) 이렇게 composition도 가능하다. allEq라는 메서드를 만들어서 사용가능
                .fetch();

        return result;
    }

    private BooleanExpression usernameEq(String usernameCond) {
//        if (usernameCond == null) {
//            return null;
//        }
//        return member.username.eq(usernameCond);

        return usernameCond == null ? null : member.username.eq(usernameCond); //간단할때는 삼항연산자 !
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    //이렇게 조립이 가능하다. 메서드화 시켜서 조립을해서 사용가능
    //또한 다른 쿼리에서 재사용까지 가능
    //JAVA코드로 쿼리를 짤 수 있으므로 매우 자유롭게 조합도 가능하다.
    private Predicate allEq (String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

}