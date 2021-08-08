package hellojpa.embeded;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class EmbededMember {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    //Period
    @Embedded
    private Period workPeriod;

    //Address
    @Embedded
    private Address homeAddress;

    @ElementCollection
    @CollectionTable (name = "FAVORITE_FOOD", joinColumns =
        @JoinColumn (name = "MEMBER_ID")
    ) //FAVORITE_FOOD라는 테이블이 생긴다. 즉, 컬렉션을 DB의 테이블로 매핑하여 넣게 되는 것임. 값타입의 컬렉션을 넣을 수가 없기 때문이다.
    @Column (name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection
    @CollectionTable (name = "ADDRESS", joinColumns =
        @JoinColumn (name = "MEMBER_ID"))
    private List<Address> addressHistory = new ArrayList<>();

    @Embedded
    //embedded 타입을 중복으로 사용할 경우 atttributeOveririded를 사용하면 된다.
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "work_city")),
            @AttributeOverride(name = "street", column = @Column(name = "work_street")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "work_zipcode"))
    })
    private Address workAddress;

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

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
