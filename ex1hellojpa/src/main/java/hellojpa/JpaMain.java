package hellojpa;

import hellojpa.inheritence.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        //code
        tx.begin();

        try {

            Movie movie = new Movie();
            movie.setDirector("director");
            movie.setActor("actor");
            movie.setName("바람과 함께 사라지다 ");
            movie.setPrice(1000);

            em.persist(movie);

            em.flush();
            em.clear();

            Movie findMovie = em.find(Movie.class, movie.getId());
            System.out.println("movie = "  + findMovie);

//            Member member = new Member();
//            member.setId(2L);
//            member.setName("HelloB");
//            em.persist(member);

//            Member findMember = em.find(Member.class, 1L);
//
//            System.out.println(findMember.getId());
//            System.out.println(findMember.getName());
//
//            findMember.setName("HelloJPA");

            //JPQL은 테이블이 대상이 아니라, 객체가 대상이 된다.
//            List<Member> result = em.createQuery("select m from Member m", Member.class)
//                    .setFirstResult(0)
//                    .setMaxResults(10)
//                    .getResultList();
//
//            for (Member member : result) {
//                System.out.println(member.getName());
//            }

            //비영속
//            Member member = new Member();
//            member.setId(101L);
//            member.setName("HelloJPA");
//
//            //영속
//            System.out.println("==== BEFORE =====");
//            em.persist(member); //이때는 DB와 연결되는게 아니다, 영속성 컨텍스트에 저장되는 것이다. before와 after로 확인됨 쿼리가 안날아감
//            //em.detach(member); //영송석에서 분리
//            System.out.println("===== AFTER =====");
//            Member findMember = em.find(Member.class, 101L);
//
//            System.out.println(findMember.getId());
//            System.out.println(findMember.getName()); //select query 가 나가지 않음 왜? DB랑 통신하는게 아니라, 영속성 컨텍스트의 1차 캐시에서 조회하기 때문이다.

            tx.commit(); //이때 쿼리가 날아감
        } catch (Exception e) {
            tx.rollback();;
        } finally {
            em.close();
        }


        emf.close();
    }
}
