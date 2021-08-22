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

            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamA");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUserName("userName1" );
            member1.changeTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUserName("userName2" );
            member2.changeTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUserName("userName3" );
            member3.changeTeam(teamB);
            em.persist(member3);


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

//            String query = "select " +
//                    "case when m.age <= 10 then '학생요금'" +
//                        " when m.age >= 60 then '경로요금'" +
//                        " else '일반요금' end " +
//                    "from Member m";

//            String query = "select m.username From Member m "; //상태 필드
//            String query2 = "select m.team from Member m" ; // 단일 값 연관 경로 : 묵시적 내부 조인 발생 -> 탐색을 한번 더 할 수 잇다.
//            //웬만하면 묵시적 내부 조인이 생기게끔 만들면 좋지않다. 성능 튜닝이 쉽지 않다.
//
//            String query3 = "select t.members from Team t"; //컬렉션 값 연관 경로 : 묵시적 내부 조인발생 -> 탐색을 할 수 없다. 잘 안쓴다.
//            String query4 = "select m.username from Team t join t.membser m"; // 명시적 조인을 통해서 이렇게 탐색을 해줘야한다. query3의 보안

            String query = "";

            String sample1= "select m From Member m join fetch m.team"; //fetch join -> n+1 문제를 해결해준다.

            String sample2 = "select distinct t From Team t join fetch t.members";
            //distinct를 하면 중복이 제거된다. join하면서 data가 뻥튀기가 되므로, distinct를 하면 중복이 제거가된다.
            //하지만, 완전히 똑같아야 sql입장에서는 중복제거가 된다.
            //JPQL은 같은 식별자를 가진 Team 엔티티를 제거해준다.




            String sample3 = "select t From Team t join t.members m"; //일반 join을 하게 되면 쿼리가 n+1이 생길 수 잇다.

            String sample4 ="select m from Member m where m = :member";

            query = sample3;

            List<Team> resultString = em.createQuery(query, Team.class).getResultList();

//            Member findMember = em.createQuery(sample4, Member.class).setParameter("member", member1).getSingleResult();

//            List<Member> resultList = em.createNamedQuery("Member.findByUserName", Member.class)
//                    .setParameter("userName", "userName1")
//                    .getResultList();
//
//            for (Member member : resultList) {
//                System.out.println("member = " + member);
//            }

            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate(); //bulk 연산
            System.out.println("resultCount = " + resultCount);

//            em.createQuery("delete from Member m")
//                    .executeUpdate();
////
//            for (Object o : resultList) {
//                System.out.println("o = " + o);
//            }




//            System.out.println("member4 = " + findMember);
//            for (Team m: resultString) {
//                System.out.println("result : " + m.getName() + ", " + m.getMembers().size());
//                for (Member member : m.getMembers()) {
//                    System.out.println("->member" + member);
//                }
//            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();;
        } finally {
            em.close();
        }


        emf.close();
    }
}
