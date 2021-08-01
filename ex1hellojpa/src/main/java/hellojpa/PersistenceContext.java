package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class PersistenceContext {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        //code
        tx.begin();

        try {

//            Member member1 = new Member(150L, "A");
//            Member member2 = new Member(160L, "B");
//
//            em.persist(member1);
//            em.persist(member2); //DB와 연결하는게 아니라, 쓰기지연 SQL저장소에 쿼리가 쌓임
//            //모았다가 쿼리를 commit 하는 순간 소게됨 -> buffer를 통해 최적화 할 수 있다

//            Member member = em.find(Member.class, 150L);
//            member.setName("ZZZ"); //Dirty Checking : 변경을 감지해서 지가 알아서 update쿼리를 날려준다.

//            em.persist(member); -> 해줄필요가 없다.

//            System.out.println("=======================");


            tx.commit();
        } catch (Exception e) {

            tx.rollback();
        } finally {
            em.close();
        }


        emf.close();
    }
}
