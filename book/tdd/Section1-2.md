## 화폐 예제 (10장~17장)
### 10장 흥미로운 시간
두  `times()`의 구현이 거의 비슷하지만 아직 완전히 동일하지 않다. 그런데 바꿀 명백한 방법이 없다. 따라서 다시 팩토리 메서드를 인라인 시켜보자
- 전장에서 그럼 왜 바꾼거야?라는 의문이 드는게 당연하다
```java
 Money times(int multiplier) {
     return new Dollar(amount * multiplier, "USD");
 }
```
```java
 Money times(int multiplier) {
     return new Franc(amount * multiplier, "CHF");
 }
```
Dollar에서는 인스턴스 변수 curreny가 항상 "USD"이기 때문에 아래와 같이 쓸 수있다. (Franc도 마찬가지)
```java
 Money times(int multiplier) {
     return new Dollar(amount * multiplier, currency);
 }
```
```java
 Money times(int multiplier) {
     return new Franc(amount * multiplier, currency);
 }
```
그리고 여기서 Franc를 가질지 Money를 가질지가 정말 중요한 사실일까? 우리에겐 테스트 코드들이 있으니 직접 실험해보자.
```java
 Money times(int multiplier) {
     return new Money(amount * multiplier, currency);
 }
```
컴파일러는 추상클래스이기때문에 에러를 뱉는다. 구현해주자.
```java
Money times(int multiplier){
    return null;
}
```
테스트를 해보면 아래와 같은 에러메시지가 난다.
```
expected: <tdd.Dollar@18842f8c> but was: <tdd.Money@54a36efa>
```
Money에 toString()을 정의해서 자세히 보자.
```java
    @Override
    public String toString() {
        return "Money{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
```
> 테스트도 없이 코드를 작성한다?
> * toString()은 디버그 출력에만 쓰이기 때문에 잘못 구현됨으로써 얻게 될 리스크가 적다.
> * 이미 빨간 막대 상태인데 새로운 테스트는 작성하지 않는게 좋다.

결과를 보면 답은 맞았는데 클래스가 다르다. `equals`구현에서 class를 비교할게아닌 currency를 비교해줘야한다.
```java
expected: tdd.Dollar@14832aea<Money{amount=10, currency='USD'}> 
but was: tdd.Money@28b887c<Money{amount=10, currency='USD'}>
```
빨간 막대 상황에서는 테스트를 작성하고 싶지 않다. 하지만 **실제 모델 코드는 테스트 없이 수정하면 안된다.**
따라서 보수적인 방법을 사용하자.
1. 변경된 코드를 되돌려 초록 막대로 돌아간다. 
   ```java
    Money times(int multiplier) {
        return new Dollar(amount * multiplier, currency);
    }
   ```
   ```java
    Money times(int multiplier) {
        return new Franc(amount * multiplier, currency);
    }
   ```
2. equals()를 위한 테스트를 고치자. :arrow_right: 당연히 실패한다.
   ```java
    @Test
    public void testDifferentClassEquality(){
        assertTrue(new Money(10, "USD")
                .equals(new Dollar(10, "USD")));
    }
   ```
3. 구현코드를 고치자. :arrow_right: 성공
   ```java
    public boolean equals(Object object){
        Money money = (Money) object;
        return amount == money.amount
                && currency().equals(money.currency());
    }
   ```
4. 다시 times()에서 Money를 반환하도록 고친 후 테스트를 해보자 :arrow_right: 성공
   ```java
    Money times(int multiplier) {
        return new Money(amount * multiplier, currency);
    }
   ```
5. 두 구현이 동일해졌으니 상위클래스로 올리자!

> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * ~~$5 X 2 = &10~~
> * ~~amount를 private로 만들기~~
> * ~~Dollar 부작용(side effect)?~~
> * Money 반올림
> * ~~equals()~~
> * hashCode()
> * Equal null
> * Equal object
> * ~~5CHF X 2 = 10CHF~~
> * **Dollar/Franc 중복**
> * ~~공용 equals~~
> * ~~공용 times~~
> * ~~Franc과 Dollar 비교하기~~
> * ~~통화?~~
> * testFrancMultiplication을 지워야할까?
<hr/>

### 11장 모든 악의 근원
하위 클래스는 달랑 생성자밖에 없기 때문에 제거하자.
- 하위 클래스에 대한 참조를 Money로 바꿔보자.
```java
 static Money dollar(int amount){
     return new Money(amount, "USD");
 }

 static Money franc(int amount){
     return new Money(amount, "CHF");
 }
```
동치성 테스트를 돌려보자. (중복되는 3, 4번째 줄은 지우자.) :arrow_right: 성공
```java
 @Test
 public void testEquality(){
     assertTrue(Money.dollar(5).equals(Money.dollar(5)));
     assertFalse(Money.dollar(5).equals(Money.dollar(6)));
     // assertTrue(Money.franc(5).equals(Money.franc(5)));
     // assertFalse(Money.franc(5).equals(Money.franc(6)));
     assertFalse(Money.franc(5).equals(Money.dollar(5)));
 }
```

- 클래스 대신 currency를 비교하는 테스트 코드는 여러 클래스가 존재할 때 의미있다.
   - `testDifferentClassEquality()` 삭제
- 프랑과 달러에 대한 별도의 테스트들도 로직상 차이가 없다.
   - `testFrancMultiplication()` 삭제

> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * ~~$5 X 2 = &10~~
> * ~~amount를 private로 만들기~~
> * ~~Dollar 부작용(side effect)?~~
> * Money 반올림
> * ~~equals()~~
> * hashCode()
> * Equal null
> * Equal object
> * ~~5CHF X 2 = 10CHF~~
> * ~~Dollar/Franc 중복~~
> * ~~공용 equals~~
> * ~~공용 times~~
> * ~~Franc과 Dollar 비교하기~~
> * ~~통화?~~
> * ~~testFrancMultiplication을 지워야할까?~~
<hr/>

### 12장 드디어, 더하기
#### 큰 테스트를 작은 테스트로 줄여서 점차 발전해가자.
더하기 기능을 좀 더 간단한 예부터 시작해보자.
> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * **$5 + $5 = &10**
```java
 @Test
 public void testSimpleAddition(){
     Money sum = Money.dollar(5).plus(Money.dollar(5));
     assertEquals(sum, Money.dollar(10));
 }
```
가짜 구현을 할 수 도 있지만, 어떻게 구현해야할지 명확하다.
```java
Money plus(Money addend){
     return new Money(amount + addend.amount, currency);
 }
```
- 하지만 다중 통화 연산을 어떻게 표현해야할지 쉽지않다.
- 가지고 있는 객체가 우리가 원하는 방식으로 동작하지 않을 경우, 그 객체와 외부 프로토콜 같으면서 내부 구현은 다른 **새로운 객체(imposter, 타인을 사칭하는 사기꾼)** 을 만들 수 있다.
- 이 예제는 가능한 메타포 중, Money를 수식의 가장 작은 단위로 보고 연산이 완료되면 환율을 이용해서 결과(Expression)을 단일 통화로 축약하는 방법을 선택했다.
```java
@Test
public void testSimpleAddition(){
      Money five = Money.dollar(5);
      Expression sum = five.plus(five);
      Bank bank = new Bank();
      // reduce = 축약
      // Bank(은행)가 Expression에 환율을 적용한다
      Money reduced = bank.reduce(sum, "USD");
      assertEquals(Money.dollar(10), reduced);
}
```
#### 왜 Bank가 수행 책임을 맡는가? 
- Expression은 하려고 하는 일의 핵심 객체이며, 가능한 다른 부분에 대해서 모르도록 해야한다.
- 그렇게 해야 오랫 동안 유연할 수 있다.(테스트, 재활용, 이해하기에 모두 쉬움)
- Expression관련 오퍼레이션이 많을 거라 추측할 수 있으며, 모든 오퍼레이션을 Expression에 추가한다면 무한히 커질것이다.

컴파일을 위해 Expression 인터페이스를 만들고 Money의 `plus()`메서드에서 반환하도록 하자.
```java
public interface Expression {
}
```
```java
public class Money implements Expression{
    ...
   Expression plus(Money addend){
      return new Money(amount + addend.amount, currency);
   }
}
```
그리고 Bank 클래스에 reduce()를 구현한다.
```java
public class Bank {
    Money reduce(Expression source, String to){
        // return null; 컴파일 -> 테스트 실패
        return Money.dollar(10); // 가짜 구현 -> 테스트 성공
    }
}
```
이제 **리팩토링**을 할 준비가 됐다.
<hr/>

### 13장 진짜로 만들기

#### 모든 중복이 제거되기 전까지 테스트는 통과한 것이 아니다.
코드중복은 없지만 데이터중복이 존재한다. 일단 순방향으로 작업해보자.

```java
public class Bank {
   Money reduce(Expression source, String to){
      return Money.dollar(10); // five.plus(five)
   }
}
```

> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * **$5 + $5 = &10**
> * $5 + $5에서 Money 반환하기

#### Sum 객체 생성을 강제하자.
`Money.plus()`는 Money가 아닌 Expression(Sum)을 반환해야한다.
```java
 @Test
 public void testPlusReturnsSum(){
     Money five = Money.dollar(5);
     Expression sum = five.plus(five);
     Sum sum = (Sum) result;
     assertEquals(five, sum.augend); // 첫번째 인자
     assertEquals(five, sum.addend); // 두번째 인자
 }
```
테스트코드 컴파일을 위해 Sum클래스를 만들고 Money.plus()를 수정하자.
```java
public class Sum implements Expression{

   Money augend;
   Money addend;

   public Sum(Money augend, Money addend) {
      this.augend = augend;
      this.addend = addend;
   }
}

```
- Money
```java
Expression plus(Money addend) {
 return new Sum(this,addend);
}
```
이제 `Bank.reduce()`는 Sum을 전달받는다.   
만약 Money의 통화가 모두 동일하고, reduce를 통해 얻어내고자 하는 Money의 통화 역시 같다면?
- 결과는 Sum내의 Money들의 amount를 합친 값을 갖는 Money객체여야한다.
```java
@Test
public void testRedueceSum(){
   Expression sum = new Sum(Money.dollar(3), Money.dollar(4));
   Bank bank = new Bank();
   Money result = bank.reduce(sum, "USD");
   assertEquals(Money.dollar(7), result);
}
```
결과는 당연히 깨진다. Bank.reduce()를 수정하자.
- Bank
```java
Money reduce(Expression source, String to) {
 Sum sum = (Sum) source;
 int amount = sum.addend.amount + sum.augend.amount;
 return Money.dollar(amount);
}
```

#### 캐스팅이 필요하다면 그 코드를 옮기자.
아래와 같은 이유로 이 코드는 지저분하다.
1. 캐스팅 : 모든 Expression에 대해 작동해야한다.
2. 공용필드와 두단계에 걸친 레퍼런스.

일부를 Sum에게 위임하자. 테스트는 모두 통과한다.
- Bank
```java
Money reduce(Expression source, String to) {
 Sum sum = (Sum) source;
 return sum.reduce(to);
}
```
- Sum
```java
  public Money reduce(String to){
    int amount = addend.amount + augend.amount;
    return new Money(amount, to);
  }
```

> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * **$5 + $5 = &10**
> * $5 + $5에서 Money 반환하기
> * Bank.reduce(Money)

#### 클래스 검사 제거를 위해 다형성을 사용하자.
Bank.reduce(Money)의 경우를 테스트하고 컴파일에러를 고쳐보자.
```java
 @Test
 public void testReduceMoney(){
     Bank bank = new Bank();
     Money result = bank.reduce(Money.dollar(1), "USD");
     assertEquals(Money.dollar(1), result);
 }
```
```java
  Money reduce(Expression source, String to) {
    if(source instanceof Money) return (Money) source; 
    Sum sum = (Sum) source;
    return sum.reduce(to);
  }
```
클래스를 명시적으로 검사하는 코드가 있을 땐 **다형성**을 사용하도록 바꾸는 것이 좋다.
- 인터페이스에 reduce를 추가하여 지저분한 캐스팅과 클래스 검사 코드를 제거하자.
- Expression
```java
public interface Expression {
  Money reduce(String to);
}
```
- Money
```java
@Override
public Money reduce(String to) {
 return this;
}
```
- Bank
```java
Money reduce(Expression source, String to) {
 return source.reduce(to);
}
```

> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * **$5 + $5 = &10**
> * $5 + $5에서 Money 반환하기
> * ~~Bank.reduce(Money)~~

<hr/>

### 14장 바꾸기

> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * $5 + $5 = &10
> * $5 + $5에서 Money 반환하기
> * ~~Bank.reduce(Money)~~
> * **Money에 대한 통화 변환을 수행하는 Reduce**
> * Reduce(Bank, String)

#### 2프랑을 달러로 바꾸자.
```java
@Test
public void testReduceMoneyDifferentCurrency() {
Bank bank = new Bank();
bank.addRate("CHF", "USD", 2); // 프랑/2 = 달러
Money result = bank.reduce(Money.franc(2), "USD");
assertEquals(Money.dollar(1), result);
}
```
- Money
```java
@Override
public Money reduce(String to) {
    int rate = currency.equals("CHF") && to.equals("USD") ? 2 : 1;
    return new Money(amount / rate, to);
}
```
- 이 코드로 인해, 갑자기 Money가 환율에 대해 알게 돼버렸다.
- 환율에 대한 일은 모두 Bank가 처리해야한다. 리팩토링 하자.
- Expression
```java
public interface Expression {
  Money reduce(Bank bank, String to);
}
```
- Bank
```java
public class Bank {

  Money reduce(Expression source, String to) {
    return source.reduce(this, to);
  }

  int rate(String from, String to) {
    return from.equals("CHF") && to.equals("USE") ? 2 : 1;
  }
}
```
- Sum
```java
public Money reduce(Bank bank, String to){
    int amount = addend.amount + augend.amount;
    return new Money(amount, to);
}
```
- Money
```java
@Override
public Money reduce(Bank bank, String to) {
    int rate = bank.rate(currency, to);
    return new Money(amount / rate, to);
}
```
- 하지만 아직도 2가 테스트와 코드 두 부분에 **중복**으로 나온다. 
- 중복을 없애기 위해 환율표를 Bank가 갖도록 만들자.

#### 해시테이블(환율표)의 키를 위한 객체를 만들자.
- 전용(private) 도우미(helper) 클래스
```java
private class Pair {

  private String from;
  private String to;

  public Pair(String from, String to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public boolean equals(Object o) {
    Pair pair = (Pair) o;
    return from.equals(pair.from) && to.equals(pair.to);
  }

  @Override
  public int hashCode() {
    return 0; // 최악의 해시코드, 구현하기 쉽지만 이대로 둔다면 해시 테이블에서 선형 검색과 비슷하게 수행됨.
  }
}
```

#### 환율표를 Bank에 저장하자.
```java
public class Bank {

  private Hashtable rates = new Hashtable();
  
  ...
  
  // 환율을 설정하자.
  void addRate(String from, String to, int rate) {
    rates.put(new Pair(from, to), new Integer(rate));
  }

  // 환율을 얻어내자.
  int rate(String from, String to) {
    Integer rate = (Integer) rates.get(new Pair(from, to));
    return rate.intValue();
  }
  ...
}
```
#### 이전 테스트에 빨간 막대가 발생한다.
```java
@Test
public void testReduceMoney() {
    Bank bank = new Bank();
    Money result = bank.reduce(Money.dollar(1), "USD");
    assertEquals(Money.dollar(1), result);
}
```
- USD에서 USD로 환율을 요청하면 그 값은 1이 되어야하는 문제가 발생했다.
- 또 하나의 새로운 테스트를 작성하고 이를 해결하자.
```java
@Test
public void testIdentityRate(){
  assertEquals(1, new Bank().rate("USD", "USD"));
}
```
- Bank
```java
int rate(String from, String to) {
    if(from.equals(to)) return 1;
    Integer rate = (Integer) rates.get(new Pair(from, to));
    return rate.intValue();
}
```
> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * ~~$5 + $5 = &10~~
> * $5 + $5에서 Money 반환하기
> * ~~Bank.reduce(Money)~~
> * ~~Money에 대한 통화 변환을 수행하는 Reduce~~
> * ~~Reduce(Bank, String)~~

<hr/>