package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass //속성들만 내려서 테이블에서 사용할 수 있게끔.. 필드를 상속해주는것.
public class JpaBaseEntity {

    @Column (updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    /**
     * 하지만, Spring Data JPA 에서는 prePersist, preUpdate 등을 안써도 된다. 해결해놓음
     */

    @PrePersist //persist하기전에 이벤트가 발생함
    public void prePersist () {
        //persist 하기전에 기본 설정을 해주는 것
        //등록일과 업데이트날을 현재 시간으로 맞춰 놓는것

        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate //update 하기전에 이벤트가 발생하도록 함
    public void preUpdate () {
        updatedDate = LocalDateTime.now();
    }
}
