package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

/**
 * projections
 * 인터페이스로 이렇게 구현한다.
 */

public interface UsernameOnly {

    /**
     * Open projections : entity를 다 퍼와서 select를 통해 가지고오고, 거기서 데이터를 추출하는 것 (@value를 사용하여 target으로 엔터티 그래프를 통해 조회)
     * spl 문을 사용하여
     */
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
