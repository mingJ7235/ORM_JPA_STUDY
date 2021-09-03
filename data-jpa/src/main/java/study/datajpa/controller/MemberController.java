package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

//    @GetMapping ("/members/{id}")
//    public String findMember(@PathVariable("id") Long id) {
//        Member member = memberRepository.findById(id).get();
//        return member.getUsername();
//    }

    //도메인 클래스 컨버터
    //Entity를 파라미터로 받는것. 단순 조회용으로만 사용해야함.
    /**
     * 기본적으로 스프링부트가 인젝션해준다.
     * 영한님은 권장하지는 않는다.
     * 왜? id를 주고받는 경우가 실무에서 적으며, 간단할때만 사용이 가능한 것.
     */
    @GetMapping ("/members/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    //http://localhost:8080/members?size=10&page=1&sort=id,desc&sort=username,desc
    //paging glabal 설정을 위해서yml에하면된다. 글로벌 설정
    @GetMapping ("/members")
    public Page<MemberDto> List(@PageableDefault(size = 5, sort="username")/*상세 설정 (yml에 한 글로벌 설정보다 우선한다)*/ Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> map = page.map(MemberDto::new);
        return map;
    }

    //test를 위해서 postconstruct를 하는 것
    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user"+i, i));
        }
    }
}
