package jpql;

import javax.persistence.*;

@Entity
@NamedQuery(
        name = "Member.findByUserName",
        query = "select m from Member m where m.userName = :userName"
)
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String userName;
    private int age;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "TEAM_ID")
    private Team team;

    @Enumerated (value = EnumType.STRING)
    private MemberType type;

    public void changeTeam (Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    public MemberType getType() {
        return type;
    }

    public void setType(MemberType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}
