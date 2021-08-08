package hellojpa.embeded;

import javax.persistence.*;
import java.time.LocalDateTime;

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
