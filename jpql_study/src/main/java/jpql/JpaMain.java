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

//            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
//            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
//            Query query3 = em.createQuery("select m.username, m.age from Member m");
//
//            List<Member> resultList = em.createQuery("select m from Member m", Member.class).getResultList();
//
//            for (Member result : resultList) {
//                System.out.println("result = " + result);
//            }

            Member singleResult = em.createQuery("select m from Member m where m.userName = :userName", Member.class)
                    .setParameter("userName", "userName")
                    .getSingleResult();
            System.out.println("singleResult = " + singleResult.getUserName());


            tx.commit();
        } catch (Exception e) {
            tx.rollback();;
        } finally {
            em.close();
        }


        emf.close();
    }
}
