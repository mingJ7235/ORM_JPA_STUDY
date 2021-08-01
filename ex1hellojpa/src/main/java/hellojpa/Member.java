package hellojpa;

import javax.persistence.*;

@Entity

//@Table (name = "USER")
public class Member {
    @Id @GeneratedValue
    @Column (name = "MEMBER_ID")
    private Long id;

    @Column (name = "USERNAME")
    private String username;

    @Column (name = "TEAM_ID")
    private Long teamId;

}
