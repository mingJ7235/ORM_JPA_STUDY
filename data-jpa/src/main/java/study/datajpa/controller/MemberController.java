package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping ("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    //도메인 클래스 컨버터
    /**
     * 기본적으로 스프링부트가 인젝션해준다.
     * 영한님은 권장하지는 않는다.
     * 왜? id를 주고받는 경우가 실무에서 적으며, 간단할때만 사용이 가능한 것.
     */
    @GetMapping ("/members/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    public void init() {
        memberRepository.save(new Member("userA"));
    }
}
