package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //쿼리메소드 기능
    List<Member> findByUsername (String username);

    //마법과도 같은 기능
    List<Member> findByUsernameAndAgeGreaterThan (String username, int age);

    List<Member> findHelloBy (); // 식별하기 위한 내용(이름)이 들어가도된다.

    List<Member> findTop3By(); //limit 쿼리

    @Query("select m from Member m where m.username = :username and m.age = :age") // 쿼리 문법이 틀렸을 시, App 로딩시점에서 오류가 난다. 잡기 쉽다.
    List<Member> findUser (@Param("username") String username, @Param("age") int age);

}
