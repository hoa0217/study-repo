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

  @Test
  public void processDoesNotNotifyListenerWhenNoMatch() {
    matcher.add(nonMatchingProfile);
    MatchSet set = nonMatchingProfile.getMatchSet(criteria);

    matcher.process(listener, set);

    verify(listener, never()).foundMatch(nonMatchingProfile, set);
  }
  // ...

  private Answer matchingAnswer() {
    return new Answer(question, Bool.TRUE);
  }

  private Answer nonMatchingAnswer() {
    return new Answer(question, Bool.FALSE);
  }
}