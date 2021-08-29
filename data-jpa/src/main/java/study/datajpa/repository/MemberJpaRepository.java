package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save (Member member) {
        //save test
        em.persist(member);
        return member;
    }

    public void delete (Member member) {
        em.remove(member);
    }

    public List<Member> findAll () {
        //JPQL
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findById (Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count () {
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThn (String username, int age) {
        return em.createQuery(
                "select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    //Paging

    /**
     * 나이가 10살이고, 이름순서대로 내림차순
     */
    public List<Member> findByPage (int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset) //몇번째부터
                .setMaxResults(limit) //몇개를 가져오겟다
                .getResultList();
    }

    //토탈 카운트를 가져와야함
    public Long totalCount (int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    /**
     * 회원나이를 한버에 변경하는 벌크연산
     */
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age+1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }

}

