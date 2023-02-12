package test.iloveyouboss;

import iloveyouboss.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ProfileTest {
    private Profile profile;
    private BooleanQuestion question;
    private Criteria criteria;

    @Before
    public void create(){
        profile = new Profile("Bull Hockey, Inc."); // 면접자
        question = new BooleanQuestion(1, "Got bonuese?"); // 면접질문
        criteria = new Criteria();
    }


    @Test
    public void matchAnswersFalseWhenMustMatchCriteriaNotMet(){

        profile.add(new Answer(question, Bool.FALSE));
        criteria.add(new Criterion(
                new Answer(question, Bool.TRUE),
                Weight.MustMatch)
        );

        assertFalse(profile.matches(criteria));
    }

    @Test
    public void matchAnswersTrueForAnyDontCareCriteria(){

        profile.add(new Answer(question, Bool.FALSE));
        criteria.add(new Criterion(
                new Answer(question, Bool.TRUE),
                Weight.DontCare)
        );

        assertTrue(profile.matches(criteria));
    }



}