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

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUserName("userName" );
            member.setAge(10);
            member.setType(MemberType.USER);

            member.changeTeam(team);

            em.persist(member);

            em.flush();
            em.clear();
//
//            String query1 = "select t from Member m inner join m.team t";
//            String query2 = "select t from Member m left join m.team t";
//            String query3 = "select t from Member m join m.team t";
//
//            String enumquery1 = "select m.userName from Member m" +
//                    " where m.type = jpql.MemberType.ADMIN"; //enum 이면 이렇게 패키지명을 다 적어줘야 한다.
//            String enumquery2 = "select m.userName from Member m" +
//                    " where m.type = :userType"; //이렇게 넣어주는 경우도 있다.
//
//            List<Member> resultEnumMember = em.createQuery(enumquery2, Member.class)
//                    .setParameter("userType", MemberType.ADMIN)
//                    .getResultList();
//
//
//            List<Member> resultList = em.createQuery(query1, Member.class)
////                    .setFirstResult(10)
////                    .setMaxResults(20)
//                    .getResultList();

//            System.out.println("resultList size = " + resultList.size());
//            for (Member member1 : resultList) {
//                System.out.println("member1 = " + member1);
//            }

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

//
//            List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.userName, m.age) from Member m", MemberDTO.class).getResultList();//dto로 반환시키는 빵법
//            MemberDTO memberDTO = resultList.get(0);
//            System.out.println("memberDTO = " + memberDTO);
//            System.out.println("memberDTO.name = " + memberDTO.getUsername());
//            System.out.println("memberDTO.age = " + memberDTO.getAge()); //스칼라

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

            String query = "select " +
                    "case when m.age <= 10 then '학생요금'" +
                        " when m.age >= 60 then '경로요금'" +
                        " else '일반요금' end " +
                    "from Member m";

            List<String> resultString = em.createQuery(query, String.class).getResultList();

            for (String s : resultString) {
                System.out.println("요금 : " + s);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();;
        } finally {
            em.close();
        }


        emf.close();
    }
}
