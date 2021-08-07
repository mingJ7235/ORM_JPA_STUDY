package hellojpa.inheritence;

import javax.persistence.*;

@Entity
//@Inheritance(strategy = InheritanceType.JOINED) //상속 -> join전략으로 하는 것. 이 어노테이션을 쓰지 않으면 단일 테이블 전략이 기본이다.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn // DTYPE이 디폴트값이다. 이 어노테이션을 써줘야 상속받은 클래스명이 나오게 된다. 해주는 것이 좋다
                        //단일 테이블 전략일때에는 discriminatorColumn이 없어도 무조건 들어가도록 설계되어있다. 없으면 단일테이블에서 구분할 수가 없다.
public abstract class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private int price;

    /**
     * JPA의 상속시 기본전략은 join할 때 싱글 테이블 전략을 사용한다.
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
