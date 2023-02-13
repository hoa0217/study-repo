package iloveyouboss;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class AssertTest {
    class InsufficientFundsException extends RuntimeException{
        public InsufficientFundsException(String message) {
            super(message);
        }

        private static final long serialVersionUID = 1L;
    }

    class Account {
        int balance;
        String name;

        Account(String name) {
            this.name = name;
        }

        void deposit(int dollars) {
            balance += dollars;
        }

        void withdraw(int dollars) {
            if (balance < dollars) {
                throw new InsufficientFundsException("balance only " + balance);
            }
            balance -= dollars;
        }

        public String getName() {
            return name;
        }

        public int getBalance() {
            return balance;
        }

        public boolean hasPositiveBalance() {
            return balance > 0;
        }
    }

    class Customer {
        List<Account> accounts = new ArrayList<>();

        void add(Account account) {
            accounts.add(account);
        }

        Iterator<Account> getAccounts() {
            return accounts.iterator();
        }
    }

    private Account account;

    @Before
    public void createAccount() {
        account = new Account("an account name");
    }

    @Test
    public void hasPositiveBalance(){
        account.deposit(50);
        assertTrue(account.hasPositiveBalance());
    }

    @Test
    // 테스트 이름은 검증하려는 동작에 관한 일반적인 설명.
    public void depositIncreasesBalance(){
        int initialBalance = account.getBalance();
        account.deposit(100);
        assertTrue(account.getBalance() > initialBalance);
        assertThat(account.getBalance(), equalTo(100));
    }

    @Test
    public void depositIncreasesBalance_hamcrestAssertTrue(){
        account.deposit(50);
        assertThat(account.hasPositiveBalance(), is(true));
    }

    @Ignore
    @Test
    public void assertFailure(){
        assertThat(account.getName(), startsWith("xyz"));
    }

    @Ignore
    @Test
    public void comparesArraysFailing() {
        assertThat(new String[] {"a", "b", "c"}, equalTo(new String[] {"a", "b"}));
    }

    @Test
    public void comparesArraysPassing() {
        assertThat(new String[] {"a", "b"}, equalTo(new String[] {"a", "b"}));
    }

    @Ignore
    @Test
    public void comparesCollectionsFailing() {
        assertThat(Arrays.asList(new String[] {"a"}),
                equalTo(Arrays.asList(new String[] {"a", "ab"})));
    }

    @Test
    public void comparesCollectionsPassing() {
        assertThat(Arrays.asList(new String[] {"a"}),
                equalTo(Arrays.asList(new String[] {"a"})));
    }

    @Test
    public void variousMatcherTests() {
        Account account = new Account("my big fat acct");
        assertThat(account.getName(), is(equalTo("my big fat acct")));

        assertThat(account.getName(), allOf(startsWith("my"), endsWith("acct")));

        assertThat(account.getName(), anyOf(startsWith("my"), endsWith("loot")));

        assertThat(account.getName(), not(equalTo("plunderings")));

        assertThat(account.getName(), is(not(nullValue())));
        assertThat(account.getName(), is(notNullValue()));

        assertThat(account.getName(), isA(String.class));

        assertThat(account.getName(), is(notNullValue())); // not helpful
        assertThat(account.getName(), equalTo("my big fat acct"));
    }

    @Test
    public void testWithWorthlessAssertionComment() {
        account.deposit(50);
        assertThat("account balance is 100", account.getBalance(), equalTo(50));
    }

    @Test(expected = InsufficientFundsException.class)
    public void throwsWhenWithdrawingTooMuch(){
        account.withdraw(100);
    }

    @Test
    public void throwsWhenWithdrawingTooMuchTry() {
        try {
            account.withdraw(100);
            fail();// 예외가 발생하지 않으면 실패
        }
        catch (InsufficientFundsException expected) {
            assertThat(expected.getMessage(), equalTo("balance only 0"));
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void exceptionRule() {
        thrown.expect(InsufficientFundsException.class);
        thrown.expectMessage("balance only 0");

        account.withdraw(100);
    }


}
