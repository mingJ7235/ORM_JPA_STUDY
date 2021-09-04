package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Autowired TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember () {

        System.out.println("memberRepository = " + memberRepository.getClass());

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan () {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery () {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList () {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

    }

    @Test
    void findMemberDto() {
        Team team = new Team ("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeamss(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames () {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member name = " + member);
        }
    }

    /**
     * Spring Data JPA 는 반환타입이 매우 유연하게 적용된다.
     */
    @Test
    public void returnTypeTest () {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA"); //컬렉션조회

        List<Member> result = memberRepository.findListByUsername("dfssfds");//빈 컬렉션을 조회하는경우
        System.out.println(result.size()); //null이아니다. 없으면 EmptyCollection이 조회된다.


        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        Member nullmember = memberRepository.findMemberByUsername("ㅁㄴㅇㅁㄴㅇㅁㄴ");//단건조회는 없을경우 null을 반환한다.

        Optional<Member> optionalmember = memberRepository.findOptionalByUsername("asease"); //optional로 반환하는 것이 가장 좋다. null일경우 Optional.empty가 찍힌다.

        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");//AAA가 두명일경우 Exception이 터진다.


    }

    @Test
    public void paging () {

        //given
        for (int i = 0; i < 10; i++) {
            memberRepository.save(new Member("member"+i, 10));
        }

        int age = 10;
        //0부터 페이지가 시작함
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //PageRequest는 Pageable을 구현한다.

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); //totalcount를 가져올 필요도없다.
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest); //count쿼리를 날리지 않는다. limit를 +1 해서 조회해준다.

        /**
         * page의 Member엔티티를 그대로 반환하면안된다.
         * dto로 변환해야한다.
         */
        Page<MemberDto> dtoPage = page.map(MemberDto::new); //dto는 api로 반환 가능

        //then
        List<Member> content = page.getContent(); //내부의 컨텐츠를 가져온다.
        //long totalElements = page.getTotalElements(); //total count다 //slice에는 없다.



//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(10); //slice에는 없다.
        assertThat(page.getNumber()).isEqualTo(0); //page 넘버도 가져올수있다. 엄청남.
        assertThat(page.getTotalPages()).isEqualTo(4); //총 페이지는 몇인지 알려줌 //slice에는 없는 기능임
        assertThat(page.isFirst()).isTrue(); //첫번째 페이지냐?
        assertThat(page.hasNext()).isTrue(); // 다음페이지가 잇냐?


    }
    @Test
    public void bulkUpdate () {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 22));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        //em.flush(); Repository interface에서 @Modifying 에서 clearAutomatically 옵션을 넣어줘야한다.
        //em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0); //요놈이 22일까 23일까. 영속성 컨텍스트!
        System.out.println("member5 age = " + member); //업데이트 벌크연산을 하면 바로 DB에 때려버리는데, 영속성 컨텍스트에는 바뀌지않은 정보가 남아있다.
        /**
         * 즉, 1차 캐시에 저장되어있다.
         * 영속성 컨텍스트를 무시하고 DB에 update를 때린다.e
         * 그러므로 벌크연산 후에 영속성컨텍스트를 날려야한다. -> em.flush(), em.clear(). 해줘야한다.
         * 영속성컨텍스트에 있는것을 날려버린다.
         * 하지만 Spring data jpa는 인터페이스에 @Modifying (clearAutomatically = true) 옵션을 주면 생략가능하다.
         */
        //이걸 주의해야한다.

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy () {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        //select Member 만 조회함. 이 쿼리만 나감
        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());

            //Team을 touch함. -> proxy를 조회
            System.out.println("member.teamClass = " + member.getTeamss().getClass());
            //진짜 안의 데이터를 가져오기위해서 DB에서 조회.
            System.out.println("member.getTeam().getName() = " + member.getTeamss().getName()); //N+1문제
            //실무에서 많이 보게되는 문제
        }
    }

    @ Test
    public void queryHint () {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername()); //최적화를 해버려서 snapshot을 안만든다. 그래서 변경이되지않는다. 오로지 readonly
        findMember.setUsername("member2"); //readonly이므로 변경감지 즉, 더티체킹을 안한다. 즉, update가 되지 않는다.
        em.flush();
    }

    @Test
    public void lock () {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername(member1.getUsername());
    }

    @Test
    public void callCustom () {
        //custom 한 인터페이스를 만들고, 구현할 class를 만든 후에 구현. 그 후에 custom한 인터페이스를 MemberRepository가 상속받도록한다.
        //QueryDSL에서 많이 사용된다. 이럴때 custom에서 많이 사용한다.
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic  () {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample () {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);
        
        em.flush();
        em.clear();
        
        //when
        //Probe 생성 prob란 필드에 데이터가 있는 실제 도메인 객체
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeamss(team); //연관관계를

        /**
         * join이 잘안된다. inner join까지만 가능함.
         * 복잡한 left, outer join이 필요할 때, 걷어내야한다.
         * - 장점
         * 동적쿼리를 편리하게 처리가 가능하다.
         * 도메인 객체를 그대로 사용할 수 있다.
         * 데이터 저장소를 RDB에서 nosql로 변경해도 코드 변경없이 사용할 수 있도록 추상화 가능하다.
         *
         * - 단점
         * join은 가능하지만 inner만 가능, left join은 안된다.
         * 매칭조건이 매우 단순하다 (equal 수준만 가능하다.) 즉, 활용도가 높지않다.
         *
         * => QueryDSL을 사용하자!
         */

        ExampleMatcher match = ExampleMatcher.matching()
                .withIgnorePaths("age"); //검색하기 위해 속성을 무시한다. primitive 타입인 age는 무시하지않으면 기본값으로 0이들어가기때문에, 그 값을 조회한다.

        Example<Member> example = Example.of(member, match);//entity로 example을 사용하는 것
        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }


}