package iloveyouboss.domain;

import iloveyouboss.domain.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProfileTest {
    private Profile profile;
    private BooleanQuestion question;
    private Criteria criteria;

    @Before
    public void create(){
        profile = new Profile("Bull Hockey, Inc."); // 면접자
        question = new BooleanQuestion("Got bonuese?"); // 면접질문
        criteria = new Criteria();
    }

    @Test
    public void matches() {
        Profile profile = new Profile("Bull Hockey, Inc.");
        Question question = new BooleanQuestion("Got milk?");

        // must-match 항목이 맞지 않으면 false
        profile.add(new Answer(question, Bool.FALSE));
        Criteria criteria = new Criteria();
        criteria.add(
                new Criterion(new Answer(question, Bool.TRUE), Weight.MustMatch));

        assertFalse(profile.matches(criteria));

        // dont-care 항목에 대해서는 true
        profile.add(new Answer(question, Bool.FALSE));
        criteria = new Criteria();
        criteria.add(
                new Criterion(new Answer(question, Bool.TRUE), Weight.DontCare));

        assertTrue(profile.matches(criteria));
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