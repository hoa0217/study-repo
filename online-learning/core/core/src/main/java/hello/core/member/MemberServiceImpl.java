package hello.core.member;

// 구현체가 하나만 있을 때 Impl붙이는게 관례
public class MemberServiceImpl implements MemberService {

    // 다형성에 의해 MemoryMemberRepository 호출
    // 의존관계가 인터페이스 뿐만 아니라 구현까지 모두 의존하는 문제점이 있음
    private final MemberRepository memberRepository = new MemoryMemberRepository();

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
