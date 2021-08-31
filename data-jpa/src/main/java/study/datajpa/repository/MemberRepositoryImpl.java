package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
            //구현체의 이름에 Impl을 무조건 적어야하며, 이것을 상속하는 인터페이스인 MemberRepository의 이름을 꼭 가지고와야한다. 즉, MemberRepository + Impl을 해야한다.
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }

}
