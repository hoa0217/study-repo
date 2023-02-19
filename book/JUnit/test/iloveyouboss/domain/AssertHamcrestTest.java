package iloveyouboss.domain;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AssertHamcrestTest {

    @Test
    @Ignore
    public void assertDoubleEqual(){
        assertThat(2.32*3, equalTo(6.96));
    }

    @Test
    public void assertWithTolerance() {
        assertTrue(Math.abs((2.32 * 3) - 6.96) < 0.0005);
    }

    @Test
    public void assertDoublesCloseTo(){
        assertThat(2.32 * 3, closeTo(6.96, 0.0005));
    }
}
