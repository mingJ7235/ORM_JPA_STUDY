package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor (access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    /**
     * JPA 식별자 생성전략이 @GenerateValue없이, 직접 할당일경우 merge()가 호출된다.
     * 이것은 매우 비효율적이므로 Persistable을 사용하여 isNew 메소드를 오버라이드 해준다.
     * 주로 CreatedDate를 기준으로 오버라이드하는것이 좋다.
     */

    @Id //@GeneratedValue
    //id를 직접 이렇게 넣어줘야할 경우. created date로 isNew 메서드를 세팅해준다.
    private String id;

    @CreatedDate //persist 되기 전에 호출되는 것
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        //새로운 객체이냐 아니냐를 짜줘야한다.
        return createdDate == null; //created date가 null이라면 새로운 데이터!!
    }

}
