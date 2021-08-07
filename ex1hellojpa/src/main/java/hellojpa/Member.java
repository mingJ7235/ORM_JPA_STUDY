package hellojpa;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity

//@Table (name = "USER")
public class Member extends BaseEntity{
    @Id @GeneratedValue
    @Column (name = "MEMBER_ID")
    private Long id;

    @Column (name = "USERNAME")
    private String username;

//    @Column (name = "TEAM_ID")
//    private Long teamId;

    @ManyToOne (fetch = FetchType.LAZY) //지연로딩 -> proxy객체로 조회한다.
    @JoinColumn (name = "TEAM_ID")
    private Team team;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    //연관관계 편의 메서드 -> 양쪽 방향에 값을 설정해주자!
    public void setTeam(Team team) {
        this.team = team;
        team.getMemberList().add(this); //매우 중요 !! -> null 값이 들어갈 우려를 줄인다.
    }
    //이름 바꾼 버전. -> setter 규약을 바꿔서 사용하는걸 추천
    public void changeTeam(Team team) {
        this.team = team;
        team.getMemberList().add(this); //매우 중요 !! -> null 값이 들어갈 우려를 줄인다.
    }
}
