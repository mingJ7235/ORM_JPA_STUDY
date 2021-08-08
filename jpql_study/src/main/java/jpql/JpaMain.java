package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Member member = new Member();
            member.setUserName("userName");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

//            List<Member> result = em.createQuery("select m from Member m", Member.class)
//                    .getResultList();
//            Member findMember = result.get(0);
//            findMember.setAge(20); //영속성 컨텍스트에서 다 관리 된다.
//
//            List<Team> resultList = em.createQuery("select m.team from Member m", Team.class)
//                    .getResultList();
//
//            List<Team> resultList1 = em.createQuery("select t from Member m join m.team t", Team.class)
//                    .getResultList(); //이렇게 쿼리를 날려야 성능이슈가 없다. join을 최대한 튜닝해서 써주는게 좋다. join을 명시적으로 해주는 것이 좋다.
//
//            em.createQuery("select o.address from Order o", Address.class).getResultList(); // 임베디드 타입 프로젝션
//
//            List scalaResult1 = em.createQuery("select m.userName, m.age from Member m").getResultList();//스칼라 타입 프로젝션 -> 반환 타입을 쭈지 않는다. String, int 혼합이므로
//            List<Object[]> scalaResult2 = em.createQuery("select m.userName, m.age from Member m").getResultList();//스칼라 타입 프로젝션 -> 반환 타입을 쭈지 않는다. String, int 혼합이므로


            List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.userName, m.age) from Member m", MemberDTO.class).getResultList();//dto로 반환시키는 빵법
            MemberDTO memberDTO = resultList.get(0);
            System.out.println("memberDTO = " + memberDTO);
            System.out.println("memberDTO.name = " + memberDTO.getUsername());
            System.out.println("memberDTO.age = " + memberDTO.getAge());

//            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
//            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
//            Query query3 = em.createQuery("select m.username, m.age from Member m");
//
//            List<Member> resultList = em.createQuery("select m from Member m", Member.class).getResultList();
//
//            for (Member result : resultList) {
//                System.out.println("result = " + result);
//            }

//            Member singleResult = em.createQuery("select m from Member m where m.userName = :userName", Member.class)
//                    .setParameter("userName", "userName")
//                    .getSingleResult();
//            System.out.println("singleResult = " + singleResult.getUserName());


            tx.commit();
        } catch (Exception e) {
            tx.rollback();;
        } finally {
            em.close();
        }


        emf.close();
    }
}
