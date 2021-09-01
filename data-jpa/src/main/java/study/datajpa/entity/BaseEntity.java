package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Spring data jpa에서는 어노테이션 @createdDate, @lastmodifiedDate 로 이벤트처리를 해결했다.
 * 또한, entitylisteners 를 해야한다.
 */

@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public class BaseEntity {

    @CreatedDate
    @Column (updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    /**
     * 등록한사람, 수정한사람
     */
    @CreatedBy
    @Column (updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

}
