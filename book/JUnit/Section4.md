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

### 멀티스레드 코드 테스트
- 멀티스레드 코드 테스트
    - 동시성 처리가 필요한 어플리케이션 코드를 테스트하는 것은 단위테스트의 영역이 아니다.
        - 통합테스트로 분류하는 것이 낫다.
    - 스레드를 사용하는 테스트 코드는 느리다.
        - 동시성 문제가 없다는 것을 보장하면서 실행 시간의 범위를 확장해야하기 때문.
- 멀티스레드 코드를 테스트하는 방법
    - 스레드와 어플리케이션 코드 사이의 중첩을 최소화하라. (이책에서 다룬 부분)
        - 스레드없이 다량의 어플리케이션 코드를 단위테스트 할 수 있도록 설계 변경하라.
        - 남은 작은 코드에 대해 스레드 집중 테스트를 작성하라.
    - 다른 사람의 작업을 믿어라.
        - java.util.concurrent패키지는 2004년 이후 충분히 오랜시간 검증받았다.
- 예시코드
    - 각 `MatchSet`에 별도의 스레드를 생성하여 `matches()`반환값을 확인한다.
    - `findMatchingProfiles()`메서드는 어플리케이션 로직과 스레드 로직을 둘 다 사용한다.
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
> 이미 검증된 process메서드를 BiConsumer인터페이스로 참조하여 MatchSet을 처리하는 로직을 호출하는 방식
- 스레드 로직을 위한 테스트 작성
```java
  @Test
  public void gathersMatchingProfiles() {

    // (1) 리스너가 수신하는 MatchSet 객체들의 프로파일 ID목록을 저장할 문자열 Set객체를 생성
    Set<String> processedSets =
        Collections.synchronizedSet(new HashSet<>());
    BiConsumer<MatchListener, MatchSet> processFunction =
        // (2) processFunction()함수를 정의 (프로덕션 코드를 대신 한다.)
        (listener, set) -> {
          // (3) 리스너에 대한 각 콜백에서 MatchSet 객체의 프로파일 ID를 processedSets에 추가
          processedSets.add(set.getProfileId());
        };
    // (4) 도우미 메서드를 사용하여 테스트옹 MatchSet객체들을 생성한다.
    List<MatchSet> matchSets = createMatchSets(100);

    // (5) 인수로 함수를 갖는 findMatchingProfiles()메서드를 호출하고 구현한 processFunction을 넘긴다.
    matcher.findMatchingProfiles(criteria, listener, matchSets, processFunction);

    // (6) 모든 스레드의 실행이 완료될 때 까지 반복문을 실행한다.
    while (!matcher.getExecutor().isTerminated()){}
    // (7) processedSets과 matchSet객체의 ID와 매칭되는지 검증한다.
    assertThat(processedSets, equalTo(matchSets.stream()
        .map(MatchSet::getProfileId).collect(Collectors.toSet())));
  }
```
> 애플리케이션 로직과 스레드 로직의 관심사를 분리하여 상당히 짧은 순서로 테스트를 작성하였다.     
> 특히 스레드 중심 테스트를 처리하는데 도움이 되는 유틸리티 메서드들을 만들면서 스레드 관련 테스트도 쉬워졌다.
---
### 데이터베이스 테스트
- 자바 영속성 API(JPA)를 사용하여 데이터베이스와 통신하는 Controller변수를 활용한 테스트를 작성해야한다.
- 현 Question Controller를 보면 대부분의 로직은 JPA의 단순한 위임이다.
    - JAP의 의존성을 고립시켰기 때문에 좋은 설계이나, 테스트 관점에서 `테스트를 작성하는게 의미가 있을까?`라는 의문이 생긴다.
```java
public class QuestionController {
   private Clock clock = Clock.systemUTC();
   // ...

   private static EntityManagerFactory getEntityManagerFactory() {
      return Persistence.createEntityManagerFactory("maria-ds");
   }
   public Question find(Integer id) {
      return em().find(Question.class, id);
   }
   
   public List<Question> getAll() {
      return em()
         .createQuery("select q from Question q", Question.class)
         .getResultList();
   }
   
   public List<Question> findWithMatchingText(String text) {
      String query = 
         "select q from Question q where q.text like '%" + text + "%'";
      return em().createQuery(query, Question.class) .getResultList();
   }
   
   public int addPercentileQuestion(String text, String[] answerChoices) {
      return persist(new PercentileQuestion(text, answerChoices));
   }

   public int addBooleanQuestion(String text) {
      return persist(new BooleanQuestion(text));
   }

   void setClock(Clock clock) {
      this.clock = clock;
   }
   // ...

   void deleteAll() {
      executeInTransaction(
         (em) -> em.createNativeQuery("delete from Question")
                   .executeUpdate());
   }
   
   private void executeInTransaction(Consumer<EntityManager> func) {
      EntityManager em = em();

      EntityTransaction transaction = em.getTransaction();
      try {
         transaction.begin();
         func.accept(em);
         transaction.commit();
      } catch (Throwable t) {
         t.printStackTrace();
         transaction.rollback();
      }
      finally {
        em.close();
      }
   }
   
   private int persist(Persistable object) {
      object.setCreateTimestamp(clock.instant());
      executeInTransaction((em) -> em.persist(object));
      return object.getId();
   }
   
   private EntityManager em() {
      return getEntityManagerFactory().createEntityManager();
   }
}
```
- 그 대신 진짜 DB와 성공적으로 상호작용하는 테스트를 작성한다.
    - 느린테스트이지만, 올바르게 연결되었음을 증명할 수 있다.
        - 자바코드, 매핑설정(persistence.xml), DB
#### 데이터 문제
- 진짜 DB와 상호작용하는 통합 테스트를 작성할 때 데이터와 이를 어떻게 가져올지는 매우 중요한 고려사항이다.
- 적절한 질의(query)결과가 나온다고 증명하려면 _먼저 적절한 데이터를 생성하고 관리해줘야한다._
- 가장 간단한 경로는 테스트마다 깨끗한 데이터베이스로 시작하는 것
    - 매 테스트는 자기가 쓸 데이터를 추가하고 그것으로 작업한다.
    - 이렇게하면 테스트간 의존성 문제를 최소화할 수 있다.
- 데이터베이스가 트랜잭션(transaction)을 지원한다면 테스트마다 트랜잭션을 초기화하고, 테스트가 끝나면 롤백한다.
    - 트랜잭션 처리는 보통 `@Before`와 `@After`메서드에 위임한다.
> 통합테스트는 필수적이지만 설계와 유지보수가 까다롭다. 단위테스트에서 검증하는 로직을 최대화하는 방향으로 통합테스트 개수와 복잡도를 최소화하자.
#### 클린 룸 데이터베이스 테스트
- 코드는 `@Before`와 `@After`모두에서 `deleteAll()`메서드를 호출한다.
    - 테스트 완료 후 데이터를 보고싶다면 `@After`의 `deleteAll()`를 주석처리한다.
- 이 테스트는 어플리케이션의 기능을 테스트하는 것이 아니라 질의(query)기능을 테스트하고 있는 것.
- 암시적으로 controller가 DB에 항목들을 잘 추가하고 있는지 검증한다.
```java
public class QuestionControllerTest {

  private QuestionController controller;

  @Before
  public void create() {
    controller = new QuestionController();
    controller.deleteAll();
  }

  @After
  public void cleanup() {
    controller.deleteAll();
  }

  @Test
  public void findsPersistedQuestionById() {
    int id = controller.addBooleanQuestion("question text");

    Question question = controller.find(id);

    assertThat(question.getText(), equalTo("question text"));
  }
   ...
}
```
#### controller 목처리
- 위의 테스트는 DB와의 모든 상호작용을 QuestionController클래스로 고립시키고 테스트했다.
- QuestionController와 상호작용하는 StatCompiler의 `questionText()`를 테스트해보자.
    - 참고로 QuestionController클래스는 이제 믿을 수 있으므로 `find()`메서드를 안전하게 스텁으로 만든다.
```java
public Map<Integer,String> questionText(List<BooleanAnswer> answers) {
  Map<Integer,String> questions = new HashMap<>();
  answers.stream().forEach(answer -> {
     if (!questions.containsKey(answer.getQuestionId()))
        questions.put(answer.getQuestionId(), 
           controller.find(answer.getQuestionId()).getText()); });
  return questions;
}
```
> 목에 대해 가정을 세운다고 생각하라. 테스트에 잘못된 가정을 포함시키면 안된다.
- Mokito를 활용한 테스트
```java
public class StatCompilerTest {
  ...
   @Mock private QuestionController controller;
   @InjectMocks private StatCompiler stats;

   @Before
   public void initialize() {
      stats = new StatCompiler();
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void questionTextDoesStuff() {
      when(controller.find(1)).thenReturn(new BooleanQuestion("text1"));
      when(controller.find(2)).thenReturn(new BooleanQuestion("text2"));
      List<BooleanAnswer> answers = new ArrayList<>();
      answers.add(new BooleanAnswer(1, true));
      answers.add(new BooleanAnswer(2, true));

      Map<Integer, String> questionText = stats.questionText(answers);

      Map<Integer, String> expected = new HashMap<>();
      expected.put(1, "text1");
      expected.put(2, "text2");
      assertThat(questionText, equalTo(expected));
   }
}
```
> Mokito에 대해 잘 알지 못해도 테스트를 읽고 빠르게 그 의도를 이해할 수 있다.

#### 결론
- **관심사를 분리**하라. 특히 어플리케이션 로직은 스레드, 데이터베이스 혹은 문제를 일으킬 수 있는 다른 의존성과 분리해야한다.
    - 의존적인 코드는 고립시켜서 테스트하라.
- 느리거나 휘발적인 코드는 **목으로 대체**하여 단위테스트의 의존성을 끊어라.
- 필요한 경우 **통합 테스트**로 작성화되, 단순하고 집중적으로 만들어라.
---
### 프로젝트에서 테스트
#### 단위테스트표준
- 코드를 체크인하기 전에 어떤 테스트를 실행해야 할지 여부
- 테스트 클래스와 메서드의 이름 짓는 방식
- 햄크레스트 혹은 전통적인 단언 사용 여부
- AAA 사용 여부
- 선호하는 목 도구
#### 지속적통합(CI)
- 코드를 더 자주 통합하고 그 결과를 매번 검증하는 것
- 코드를 변경점과 합쳤을 때 동작하지 않는다는 것을 빨리 알 수 있음
- CI는 지속적 통합 서버라는 도구의 지원을 받아야한다.
    - CI 서버는 소스 저장소를 모니터링한다.
    - 새로운 코드가 체크인되면 CI서버는 소스 저장소에서 코드를 가져와 빌드를 초기화한 후 문제가 있다면 개발팀에 통지한다.
      - 이 때 빌드가 **단위테스트**를 함께 수행해야한다.
    - ex) 허드슨, 젠킨스, 팀시티, 앤트힐, 크루즈콘드롤 등
#### 코드 커버리지
- 코드 커버리지는 단위 테스트가 실행한 코드의 전체 퍼센트를 측정하는 것
    - ex) 엠마, 코버투라 등
- 코드커버리지는 어느정도가 적당할까?
    - 습관적으로 단위테스트를 작성하는 팀들은 쉽게 70% 이상의 커버리지를 달성한다.
    - 1/3정도 테스트 되지 않은 상태인데 이는 나쁜 의존성 때문에 그 코드가 테스트하기 어렵기 때문이다.
        - 하지만 이런 테스트되지 않은 코드가 결함을 숨기고 있기 마련..
    - 설계가 좋을 수록 테스트 작성도 쉬워진다. TDD를 수행하는 개발자들은 정의상 90%를 초과달성한다.
        - 이들은 작성하는 코드를 설명하기위해 단위테스트를 항상 먼저 작성한다.
    - 하지만 커버리지 퍼센트가 높다고 해서 무조건 좋은테스트만 있는게 아니다.
        - 커버리지를 높이기 위해 나쁜 테스트, 가치없는 테스트를 작성했을 수 도 있다.
    - 결론 : 테스트 작성을 완료했다고 생각했을 때 커버리지 도구를 실행하라.
        - 아직 커버되지 않은 영역을 보고 염려가 된다면 테스트를 작성하라.
        - 숫자 자체로는 큰 의미가 없지만 추세는 중요하다.
        - 시간이 지날 수록 커버리지 퍼센트가 높아져야하고 적어도 아래방향으로 내려가면 안된다.

    