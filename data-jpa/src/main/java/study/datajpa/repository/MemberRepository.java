package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {
    //쿼리메소드 기능
    List<Member> findByUsername (String username);

    //마법과도 같은 기능
    List<Member> findByUsernameAndAgeGreaterThan (String username, int age);

    List<Member> findHelloBy (); // 식별하기 위한 내용(이름)이 들어가도된다.

    List<Member> findTop3By(); //limit 쿼리

    @Query("select m from Member m where m.username = :username and m.age = :age") // 쿼리 문법이 틀렸을 시, App 로딩시점에서 오류가 난다. 잡기 쉽다.
    List<Member> findUser (@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m ")
    List<String> findUsernameList();

    //DTO를 반환하려고할때는 이렇게 new operation을 통해서 반환시켜야한다.
    //but, QueryDSL을 사용하면 이것도 편해진다.
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.teamss t")
    List<MemberDto> findMemberDto();

    @Query ("select m from Member m where m.username in :names")
    List<Member> findByNames (@Param("names") Collection<String> names);

    /**
     * Spring jpa는 반환타입이 아주 유연하다.
     */

    List<Member> findListByUsername (String username); //컬렉션
    Member findMemberByUsername (String username); //단건
    Optional<Member> findOptionalByUsername (String username); //단건 optional

    @Query ("select count(m) from Member m where m.username = :username")
    Long checkDuplicationMember (@Param("username") String username);

    /**
     * 페이지네이션
     */
//    Page<Member> findByAge (int age, Pageable pageable);

    @Query (value = "select m from Member m left join m.teamss t",
            countQuery = "select count (m) from Member m") //count 쿼리에 대해서 최적화 하는 것임. join이 없이 데이터를 깔끔하게 가져오도록 함. 실무에서 복잡할때 사용
    Page<Member> findByAge (int age, Pageable pageable);

    /**
     * bulk연산
     */
//    @Modifying(clearAutomatically = true) //이게 있어야 update가 된다. 벌크연산을 위해서 modifying 필요
//    //clearAutomatically = true : 영속성 컨텍스트를 자동적으로 날려준다. em.clear의 효과다.
//    @Query ("update Member m set m.age = m.age +1 where m.age >= :age")
//    int bulkAgePlus (@Param("age") int age);

    @Modifying (clearAutomatically = true)
    @Query ("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus (@Param("age") int age);


    /**
     * fetch join을 사용 : 연관관계인 것을 한번에 가져온다.
     * lazy 로딩을 해야하므로 이렇게 fetch join을 하는 것.
     * 객체 그래프를 join을 활용하여 다 끌고 오는 것이다.
     * fetch join을 위해서는 JPQL을 써야하는가? nono
     * Entity graph를 사용한다. !!
     */
    @Query ("select m from Member m left join fetch m.teamss")
    List<Member> findMemberFetchJoin();

    //Entity graph
    @Override
    @EntityGraph(attributePaths = {"teamss"}) //JPQL을 안짜고 함께 조회하고싶은 Entity의 연관관계를 맺은 Entity의 필드명
    List<Member> findAll();

    @EntityGraph (attributePaths = {"teamss"})
    @Query ("select m from Member m")
    List<Member> findMemberEntityGraph ();

    /**
     * 메소드네이밍쿼리 + EntityGraph (fetch join) 을 한번에 사용할 수 있음
     * 간단할때는 EntityGraph쓰고, 조금 복잡하면 JPQL의 fetch join을 사용한다.
     */
    @EntityGraph (attributePaths = {"teamss"})
    List<Member> findEntityGraphByUsername (@Param("username") String username);


    /**
     * Hints
     * Hibernate에게 hint를 준다. 오로지 읽기목적으로만 쿼리를 가져온다면, 이렇게 사용하면 메모리 최적화가 된다.
     * 성능최적화를 위해 사용을 하지만, 잘 사용하지 않는다.
     * 성능테스트 후에 결정하는 것이 좋다.
     */
    @QueryHints (value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername (String username);

    /**
     * Lock
     * 내용이 깊은 부분이다. isolation... 어려운내용 -> 책을 좀 봐야할듯
     */
    @Lock (LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername (String username);

    /**
     * Auditing : 생성, 변경할때 그렇게 한 사람과 시간을 추적 하기 위함.
     */

    /**
     * Projections
     * DTO 값만을 가져오고싶을때 유용하게 사용된다.
     * UsernameOnly라는 인터페이스를 만들어놓으면 구현체를 스프링이 반환해준다.
     * 데이터를 간단하게 원하는 것만 찍어서 가져올때 Projections를 사용된다.
     */
    List<UsernameOnlyDto> findProjectionsByUsername (@Param("username") String username);

    <T> List<T> findProjectionsGenericByUsername (@Param("username") String username, Class<T> type);

    /**
     * native query를 짜는 방법
     * but, 한계가 많다.
     * 반환 type이 몇가지 지원을 안한다.
     *
     * - JPQL 처럼 어플리케이션 로딩시점에 문법 확인이 불가하다.
     * - 동적 쿼리 불가
     *
     * projections를 사용할 수있다.
     */
    @Query (value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery (String username);

    Page<MemberProjection> findByNativeProjection


}
