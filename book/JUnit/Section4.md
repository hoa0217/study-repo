## 4부 더 큰 단위테스트 그림

### 테스트 주도 개발

- 작성하려는 코드가 있다면 항상 먼저 어떻게 그 코드를 테스트 할지 고민해야한다.
    - 코드를 작성한 후 어떻게 테스트할지 고민하기보다 작성할 코드를 묘사하는 테스트를 설계해야한다.
    - 역방향 접근법 == 테스트 주도 개발(TDD)
- TDD의 장점
    - 코드가 예상한대로 동작한다는 자신감을 얻을 수 있다.
    - 코드가 변경될 것이라는 두려움을 지울 수 있다.
        - 구현하는 실질적 모든 사례에 대해 단위테스트를 작성하게됨.
        - 코드를 지속적으로 발전시킬 수 있는 자유를 준다.
- TDD 사이클
    1. 실패하는 테스트 코드 작성하기    
       - 동작을 정의하는 테스트 코드 작성
    2. 테스트 통과시키기   
       - 컴파일 되는 수준으로만 단순하게 작성하여 대응
    3. 이전 두 단계에서 추가되거나 변경된 코드 개선하기
- TDD의 점진적 사고방식
    - 실패하는 각 테스트에 대해 그 테스트를 통과할 수 있는 작은양의 코드만 추가하라.
        - 가능한 가장 작은 증분(increment)
        - 이를 통해 실패하는 또 다른 테스트를 빠르게 만들 수 있음.
    - 테스트가 나타내는 **명세**를 정확히 코딩
        - 테스트로 시스템이 무엇을 하는지 문서화
            - 특히 테스트 이름이 깔끔하고 일관성 있을 수록 신뢰할 수 있는 클래스 문서가된다.
        - 추측에 근거한 개발이라는 잠재적 낭비를 피할 수 있음.
- 테스트 정리
    - 테스트는 짧고 깔끔해야한다.
    - 테스트를 통과시킨 후 막 작성한 코드를 리팩토링하고 정리하자.
        - ex) 객세생성부분 `@Before`로 빼기, 필드명 변경 등
    - 그 후 빠르게 새로운 테스트를 만들어 TDD 사이클을 짧게 유지하자.

> 리팩토링은 쉽지만 효과가 크다.   
> 가독성 좋은 변수명으로 변경 :arrow_right: 독자에게 큰 정보를 줌.   
> 의도를 알 수 있는 도우미 메서드로 추출 :arrow_right: 테스트 향상.

- TDD의 사이클은 짧다.
    - 만약 10분정도 제한을 걸고, TDD를 수행했으나 테스트를 통과하지 못하였다면?
    - 과감하게 코드를 폐기하고 더 작은 단계로 도전하라.

---

### 까다로운 테스트

- 멀티스레드 코드 테스트
    - 동시성 처리가 필요한 어플리케이션 코드를 테스트하는 것은 단위테스트의 영역이 아니다.
        - 통합테스트로 분류하는 것이 낫다.
    - 스레드를 사용하는 테스트 코드는 느리다.
        - 동시성 문제가 없다는 것을 보장하면서 실행 시간의 범위를 확장해야하기 때문.
- 멀티스레드 코드를 테스트하는 방법
    - 스레드 통제와 어플리케이션 코드 사이의 중첩을 최소화하라.(이책에서 다룰 부분)
        - 스레드없이 다량의 어플리케이션 코드를 단위테스트 할 수 있도록 설계 변경하라.
        - 남은 작은 코드에 대해 스레드 집중 테스트를 작성하라.
    - 다른 사람의 작업을 믿어라.
        - java.util.concurrent패키지는 2004년 이후 충분히 오랜시간 검증받았다.
- 예시코드
    - 각 `MatchSet`에 별도의 스레드를 생성하여 `matches()`반환값을 확인한다.
    - 이 메서드는 어플리케이션 로직과 스레드 로직을 둘 다 사용한다.
```java
public void findMatchingProfiles(
    Criteria criteria,MatchListener listener){
    ExecutorService executor=
    Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);

    List<MatchSet> matchSets=profiles.values().stream()
    .map(profile->profile.getMatchSet(criteria))
    .collect(Collectors.toList());
    for(MatchSet set:matchSets){
    Runnable runnable=()->{
    if(set.matches())
    listener.foundMatch(profiles.get(set.getProfileId()),set);
    };
    executor.execute(runnable);
    }
    executor.shutdown();
}
```
- 리팩토링1 (애플리케이션 로직 추출)
```java
public void findMatchingProfiles(
    Criteria criteria,MatchListener listener){
    ExecutorService executor=
    Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);

    for(MatchSet set:collectMatchSets(criteria)){
    Runnable runnable=()->process(listener,set);
    executor.execute(runnable);
    }
    executor.shutdown();
}

void process(MatchListener listener,MatchSet set){
    if(set.matches())
    listener.foundMatch(profiles.get(set.getProfileId()),set);
}

List<MatchSet> collectMatchSets(Criteria criteria){
    List<MatchSet> matchSets=profiles.values().stream()
    .map(profile->profile.getMatchSet(criteria))
    .collect(Collectors.toList());
    return matchSets;
}
```
- process 메서드에 대한 테스트 코드 작성
```java
  @Before
  public void createMatchListener() {
    // (1) 모키토의 정적 mock()메서드를 사용하여 MatchListener 목 인스턴스 생성
    listener = mock(MatchListener.class);
  }

  @Test
  public void processNotifiesListenerOnMatch() {
    // (2) 매칭되는 프로파일(주어진 조건에 매칭될 것으로 기대되는 프로파일)을 matcher변수에 추가
    matcher.add(matchingProfile);
    // (3) 주어진 조건 집합에 매칭되는 프로파일에 대한 MatchSet 객체를 요청
    MatchSet set = matchingProfile.getMatchSet(criteria);

    // (4) 목 리스너와 MatchSet객체를 넘겨 matccher 변수에 매칭 처리를 지시
    matcher.process(listener, set);

    // (5) 모키토를 활용하여 foundMatch메서드가 호출되었는지 확인
    verify(listener).foundMatch(matchingProfile, set);
  }
```
- 리팩토링2 (스레드 로직 테스트를 위한 재설계)
```java
public void findMatchingProfiles( 
     Criteria criteria, 
     MatchListener listener, 
     List<MatchSet> matchSets,
     BiConsumer<MatchListener, MatchSet> processFunction) {
  for (MatchSet set: matchSets) {
     Runnable runnable = () -> processFunction.accept(listener, set); 
     executor.execute(runnable);
  }
  executor.shutdown();
}
 
public void findMatchingProfiles( 
     Criteria criteria, MatchListener listener) { 
  findMatchingProfiles(
        criteria, listener, collectMatchSets(criteria), this::process);
}

void process(MatchListener listener, MatchSet set) {
  if (set.matches())
     listener.foundMatch(profiles.get(set.getProfileId()), set);
}

List<MatchSet> collectMatchSets(Criteria criteria) {
  List<MatchSet> matchSets = profiles.values().stream()
        .map(profile -> profile.getMatchSet(criteria))
        .collect(Collectors.toList());
  return matchSets;
}
```
