package hello.core.member;

// 구현체가 하나만 있을 때 Impl붙이는게 관례
public class MemberServiceImpl implements MemberService {

    // 다형성에 의해 MemoryMemberRepository 호출
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
