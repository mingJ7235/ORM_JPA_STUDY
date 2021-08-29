package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
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
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
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

    @Query (value = "select m from Member m left join m.team t",
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


}
