package hello.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 구현체가 하나만 있을 때 Impl붙이는게 관례
@Component
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    // 의존관계에 대한 고민은 외부에 맡기고 실행에만 집중.
    // 의존관계 주입(Dependency Injection)
    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
