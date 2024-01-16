# 중복 등록과 충돌

### 자동 빈 등록 vs 자동 빈 등록
- 컴포넌트 스캔에 의해 자동으로 빈이 등록되는데, 빈 이름이 같을 경우 스프링은 오류를 발생시킨다.
  - `ConflictingBeanDefinitionException`

### 수동 빈 등록 vs 자동 빈 등록
- **수동 빈 등록이 우선권을 가지기 때문에, 수동빈이 자동 빈을 오버라이딩 해버린다.**
```bash
Overriding bean definition for bean 'memoryMemberRepository' with a different definition: replacing [Generic bean: class [hello.core.member.MemoryMemberRepository];
```
- 개발자가 의도한 것이라면 수동이 우선권을 가지는게 좋지만 대부분은 설정들이 꼬여서 이런 결과가 만들어진다.
- 그러면 정말 잡기 어려운 버그가 만들어진다.

#### 스프링 부트의 경우
- 스프링 부트는 수동 빈 등록과 자동 빈 등록 충돌이 나면, 오류가 발생하도록 기본값을 바꾸었다.
```bash
Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true
```

