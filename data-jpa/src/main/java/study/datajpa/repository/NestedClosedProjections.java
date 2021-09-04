package study.datajpa.repository;

public interface NestedClosedProjections {

    //중첩구조

    String getUsername();
    TeamInfo getTeam(); //Team은 entity를 모두 다 가져올수밖에 없음 username은 바로 가져올수있으나, Team에서 name을 불러오기 위해서는 Team을 한번 조회해서 가져와야함.

    //즉, 엔터티가 2개가 사용되는 순간, 애매해진다. -> LeftOuter join 처리를 한다.

    interface TeamInfo {
        String getName();
    }
}
