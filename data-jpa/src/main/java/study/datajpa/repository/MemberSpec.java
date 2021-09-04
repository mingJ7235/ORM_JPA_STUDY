package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;

import javax.persistence.criteria.*;

public class MemberSpec {

    /**
     * 너무 이론적으로 해놓았기 때문에 구현기술에 jpa criteria를 써서 너무 복잡하다.
     * 가독성이 안좋고, 복잡하다.
     * 실무에서 안쓴다. 직관적이지 않다. Sql이나 JPQL을 만드는것이 목적인데, 너무 복잡함.
     * QueryDsl을 사용하자.
     * QueryDsl은 동적쿼리를 훨씬 편하게 사용가능하다
     */

    public static Specification<Member> teamName (final String teamName) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(teamName)) {
                return null;
            }
            Join<Object, Object> t = root.join("team", JoinType.INNER);//회원과 조인
            return criteriaBuilder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<Member> username (final String username) {
        return (Specification<Member>) (root, query, builder) ->
                builder.equal(root.get("username"), username);
    }

}
