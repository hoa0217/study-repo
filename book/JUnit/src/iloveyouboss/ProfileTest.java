package iloveyouboss;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    public void matchAnswersFalseWhenMustMatchCriteriaNotMet(){
        Profile profile = new Profile("Bull Hockey, Inc."); // 면접자
        Question question = new BooleanQuestion(1, "Got bonuese?"); // 면접질문

        Answer profileAnswer = new Answer(question, Bool.FALSE);// 면접자 대답
        profile.add(profileAnswer);

        Criteria criteria = new Criteria();
        Answer criteriaAnswer = new Answer(question, Bool.TRUE); // 면접관이 기대하는 대답
        Criterion criterion = new Criterion(criteriaAnswer, Weight.MustMatch); // 가중치 설정
        criteria.add(criterion);

        assertFalse(profile.matches(criteria));
    }

    @Test
    public void matchAnswersTrueForAnyDontCareCriteria(){
        Profile profile = new Profile("Bull Hockey, Inc."); // 면접자
        Question question = new BooleanQuestion(1, "Got bonuese?"); // 면접질문

        Answer profileAnswer = new Answer(question, Bool.FALSE);// 면접자 대답
        profile.add(profileAnswer);

        Criteria criteria = new Criteria();
        Answer criteriaAnswer = new Answer(question, Bool.TRUE); // 면접관이 기대하는 대답
        Criterion criterion = new Criterion(criteriaAnswer, Weight.DontCare); // 가중치 설정
        criteria.add(criterion);

        assertTrue(profile.matches(criteria));
    }



}