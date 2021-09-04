package study.datajpa.repository;

public class UsernameOnlyDto {

    private final String username;

    //parameter의 이름이 정확히 일치해야한다.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }
    public String getUsername () {
        return username;
    }
}
