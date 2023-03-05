/***
 * Excerpted from "Pragmatic Unit Testing in Java with JUnit",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/utj2 for more book information.
 ***/
package iloveyouboss;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.junit.*;
// ...
import static org.mockito.Mockito.*;
public class ProfileMatcherTest {
  // ...
  private BooleanQuestion question;
  private Criteria criteria;
  private ProfileMatcher matcher;
  private Profile matchingProfile;
  private Profile nonMatchingProfile;

  @Before
  public void create() {
    question = new BooleanQuestion(1, "");
    criteria = new Criteria();
    criteria.add(new Criterion(matchingAnswer(), Weight.MustMatch));
    matchingProfile = createMatchingProfile("matching");
    nonMatchingProfile = createNonMatchingProfile("nonMatching");
  }

  private Profile createMatchingProfile(String name) {
    Profile profile = new Profile(name);
    profile.add(matchingAnswer());
    return profile;
  }

  private Profile createNonMatchingProfile(String name) {
    Profile profile = new Profile(name);
    profile.add(nonMatchingAnswer());
    return profile;
  }

  @Before
  public void createMatcher() {
    matcher = new ProfileMatcher();
  }

  @Test
  public void collectsMatchSets() {
    matcher.add(matchingProfile);
    matcher.add(nonMatchingProfile);

    List<MatchSet> sets = matcher.collectMatchSets(criteria);

    assertThat(sets.stream().map(set -> set.getProfileId()).collect(Collectors.toSet()),
        equalTo(new HashSet<>(Arrays.asList(matchingProfile.getId(), nonMatchingProfile.getId()))));
  }

  private MatchListener listener;

  @Before
  public void createMatchListener() {
    listener = mock(MatchListener.class);
  }

  @Test
  public void processNotifiesListenerOnMatch() {
    matcher.add(matchingProfile);
    MatchSet set = matchingProfile.getMatchSet(criteria);

    matcher.process(listener, set);

    verify(listener).foundMatch(matchingProfile, set);
  }

  @Test
  public void processDoesNotNotifyListenerWhenNoMatch() {
    matcher.add(nonMatchingProfile);
    MatchSet set = nonMatchingProfile.getMatchSet(criteria);

    matcher.process(listener, set);

    verify(listener, never()).foundMatch(nonMatchingProfile, set);
  }

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

  private List<MatchSet> createMatchSets(int count) {
    List<MatchSet> sets = new ArrayList<>();
    for (int i = 0; i < count; i++)
      sets.add(new MatchSet(String.valueOf(i), null, null));
    return sets;
  }

  private Answer matchingAnswer() {
    return new Answer(question, Bool.TRUE);
  }

  private Answer nonMatchingAnswer() {
    return new Answer(question, Bool.FALSE);
  }
}