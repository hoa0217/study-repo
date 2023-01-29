# 테스트 주도 개발 - 캔트 벡

### 테스트 주도 개발 법칙
- 오직 자동화된 테스트가 실패할 경우에만 새로운 코드를 작성한다.
- 중복을 제거한다.

이 규칙에 의해 프로그래밍 순서가 정해진다.
1. 빨강 - 실패하는 작은 테스트를 작성한다. 처음엔 컴파일 조차 되지 않을 수 있다.
2. 초록 - 빨리 테스트가 통과하게끔 만든다. 이를 위해 어떤 **죄악**을 저질러도 좋다.
3. 리팩토링 - 일단 테스트를 통과하게만 하는 와중에 생겨난 모든 중복을 제거한다.
> **죄악** : 기존 코드 복사해서 붙이기(copy and paste), 함수가 무조건 특정 상수 반환하기 등
<hr/>

## 화폐 예제
### 1장 다중 통화를 지원하는 Money객체 
1. 먼저 작업해야할 테스트 목록을 작성하자.
    - 작업을 시작하면 **굵은 글씨** 로 표시
    - 작업이 끝나면 ~~취소선~~ 으로 표시
    - 다른 테스트가 생각나면 계속 항목을 추가
    
    > * $5 + 10CHF = &10 (환율이 2:1일 경우)
    > * **$5 X 2 = &10**
    
2. 객체를 먼저 만드는게 아닌, 복잡하지 않은 테스트를 먼저 시작한다.
   
   오퍼레이션이 외부에서 어떻게 보이길 원하는지를 코드로 표현한다.
   > * **$5 X 2 = &10**
   ```java
   public void testMultiplication() {
       Dollar five = new Dollar(5);
       five.times(2);
       assertEquals(10, five.amount);
   }
   ```
   
   이 코드는 public field에다가 금액계산에 정수형을 사용한다. 이와같은 문제를 할일 목록에 추가하고 계속 진행한다.
   > * $5 + 10CHF = &10 (환율이 2:1일 경우)
   > * **$5 X 2 = &10**
   > * amount를 private로 만들기
   > * Dollar 부작용(side effect)?
   > * Money 반올림
   
3. 컴파일 에러를 고치자.
     1. Dollar 클래스 없음
     2. 생성자 없음
     3. time(int)메서드 없음
     4. amount 필드 없음
    ```java
    public class Dollar { //1
        int amount; //4

        public Dollar(int amount) { //2
    
        }

        void times(int multiplier) { //3
    
        }
    }
    ```
   한번에 하나 씩 정복하며, 정말 **최소한의 구현**만 해준다.(끔찍한 죄악을..)
   
   그리고 이제 테스트를 실행해서 실패하는 모습을 볼 수 있다.
   

4. 테스트가 실패했으니 조금 수정하자.
   
   amount가 0이기 때문에 실패하였다. 우리는 여기서도 최소 작업만 해준다.
    ```java
    int amount = 10;
    ```

5. 중복을 제거하기 위해 **리팩토링**을 하자.
    > 리팩토링 : 코드의 외적 행위는 그대로 유지하면서 내부 구조를 변경하는 작업
   ```java
    int amount = 5 * 2; // new Dollar(5) , five.times(2);
    ```
   테스트에 있는 데이터와 코드가 중복되기 때문에 리팩토링을 진행한다.
    ```java
    public class Dollar {
        int amount;

        public Dollar(int amount) {
            this.amount = amount;
        }

        void times(int multiplier) {
            // amount = 5 * 2;
            // amount = amount * 2;
            // amount = amount * multiplier;
            amount *= multiplier; // 중복제거완료.
        }
    }
    ```
   첫 번째 테스트를 완료 표시하고 다음 항목을 결정한다.
   > * $5 + 10CHF = &10 (환율이 2:1일 경우)
   > * ~~$5 X 2 = &10~~
   > * amount를 private로 만들기
   > * **Dollar 부작용(side effect)?**
   > * Money 반올림
<hr/>

### 2장 타락한 객체
연산을 수행한 후 해당 Dollar의 값이 바뀐다. 이러한 부작용을 해결하기위해 `times()`가 객체를 반환하게 해보자.
- 이렇게되려면 테스트도 수정되고, Dollar의 인터페이스도 수정되야한다.
```java
public void testMultiplication() {
    Dollar five = new Dollar(5);
    Dollar product = five.times(2);
    assertEquals(10, five.amount);
    product = five.times(3);
    assertEquals(15, five.amount);
}
```
```java
 Dollar times(int multiplier) {
     // amount *= multiplier;
     // return null;
     return new Dollar(amount * multiplier);
 }
```
최대한 빨리 초록색을 보기위해 취할 수 있는 전략은 2가지다.
- 가짜로 구현하기 : 상수를 반환하게 만들고 진짜 코드를 얻을 때까지 단계적으로 상수를 변수로 바꾸어 간다.
- 명백한 구현 사용하기 : 실제 구현을 입력한다.
> 느낌(부작용에 대한 혐오감)을 테스트(하나의 Dollar 객체에 곱하기를 두 번 수행하는 것)로 변환하는 것은 TDD의 일반적인 주제이다.

> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * ~~$5 X 2 = &10~~
> * amount를 private로 만들기
> * ~~Dollar 부작용(side effect)?~~
> * Money 반올림
<hr/>

### 3장 모두를 위한 평등
Dollar는 값 객체 패턴을 사용하고 있다. 따라서 `equals`를 구현해야한다.
- $5와 $5는 똑같은 것이기 때문이다.
> 값 객체 패턴(value object pattern) : 객체를 값 처럼 쓸 수 있는데, 인스턴스 변수가 생성자를 통해 일단 설정된 후 결코 변하지 않는다.

또한 해시테이블의 키로 쓸 생각이라면 `hashcode`도 함께 구현해야한다.
> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * ~~$5 X 2 = &10~~
> * amount를 private로 만들기
> * ~~Dollar 부작용(side effect)?~~
> * Money 반올림
> * **equals()**
> * hashCode()

삼각측량을 위해 테스트를 두 개 이상 만들자.
> 삼각측량 : 라디오 신호를 두 수신국이 감지하고 있을 때, 두 수신국 사이의 거리가 알려져있고, 각 수신국이 신호의 방향을 알고 있다면 그것만으로 충분히 신호의 거리와 방향을 알 수 있다.

```java
 @Test
 public void testEquality(){
     assertTrue(new Dollar(5).equals(new Dollar(5)));
     assertFalse(new Dollar(5).equals(new Dollar(6)));
 }
```
```java
 @Override
 public boolean equals(Object object){
     // return true;
     Dollar dollar = (Dollar) object;
     return amount == dollar.amount;
 }
```
코드와 테스트 사이의 중복을 제거하고 일반적인 해법이 보이면 그 방법대로 구현하면 된다.
- 테스트를 2번 짜는 것은 비효율적이므로

하지만 설계를 어떻게할지, 리팩토링을 어떻게할지 감이 안온다면 삼각측량을 사용하자.
삼각 측량은 문제를 **다른 방향**에서 생각해볼 기회를 제공한다.

동일성 문제는 일시적으로 해결됐으나, null값이나 다른 객체들을 비교한다면 문제가 생긴다.
> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * ~~$5 X 2 = &10~~
> * **amount를 private로 만들기**
> * ~~Dollar 부작용(side effect)?~~
> * Money 반올림
> * ~~equals()~~
> * hashCode()
> * Equal null
> * Equal object
<hr/>

### 4장 프라이버시
동일성을 해결했으니, 첫번째 테스트에서 Dollar와 Dollar를 비교할 수 있다.
- 테스트와 코드사이의 결합도를 낮추기 위해, 객체의 새기능을 사용해보자.
```java
 @Test
 public void testMultiplication() {
     Dollar five = new Dollar(5);
     assertEquals(new Dollar(10), five.times(2));
     assertEquals(new Dollar(15), five.times(3));
 }
```
그리고 amount를 사용하는 코드는 Dollar 자신 뿐이므로 private로 변경하자.
```java
private int amount;
```
> * $5 + 10CHF = &10 (환율이 2:1일 경우)
> * ~~$5 X 2 = &10~~
> * ~~amount를 private로 만들기~~
> * ~~Dollar 부작용(side effect)?~~
> * Money 반올림
> * ~~equals()~~
> * hashCode()
> * Equal null
> * Equal object

하지만 **위험한 상황**을 만들었다는 점을 인식하고 진행하자.
- 만약 동일성 테스트 검증이 실패했다면, 곱하기 테스트 역시 실패할 것이다.
- 실패했다면 교훈을 얻고 다시 앞으로 나아가자.
<hr/>

### 5장 솔직히 말하자면
> * $5 + 10CHF = &10 (환율이 2:1일 경우)

이 첫번 째 테스트를 처리하기 위해 작은 단계부터 접근하자.
- 큰 테스트를 공략할 수 없기 때문에, 자그마한 테스트를 만들었다.

프랑(Franc)을 표현할 수 있는 객체가 필요하고 Dollar와 비슷하게 동작해야한다.
- 여기서 복붙이라는 엄청난 죄악이 일어나지만 고해성사 할것이다.
```java
 @Test
 public void testMultiplication() {
     Franc five = new Franc(5);
     assertEquals(new Franc(10), five.times(2));
     assertEquals(new Franc(15), five.times(3));
 }
```
엄청난 중복이 생겼고 이것들을 제거해야한다!!
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
> * Dollar/Franc 중복
> * **공용 equals**
> * 공용 times
<hr/>

### 6장 돌아온 `모두를 위한 평등`
중복을 청소하기 위해, 두 클래스의 공통 상위 클래스를 찾아보자.
- 만든 클래스 중 하나를 상속받게 하는 것은 구원하지 못한다.

Money라는 공통클래스를 만들고 `equals`를 위임하자.
- 비교하려면 amount 필드도 필요하므로 같이 위임한다.
- 하위클래스에서도 볼 수 있도록 가시성을 `protected`로 지정.
1. Money class 생성
2. Dollar, Franc의 상위클래스(Money) 지정 후, amount 변수 Money로 위임
3. equals를 제거하기 전 Dollar의 equals의 타입을 Money로 바꾼 후 Test
   ```java
    public boolean equals(Object object){
        Money money = (Money) object;
        return amount == money.amount;
    }
   ```
   -  모든 Test가 잘 돌면, Money로 equals 위임
5. Franc의 equals 타입도 Money로 바꾼 후 Test
   - 미처 작성하지 않은 Franc의 equals Test작성(이것도 복붙...)
   ```java
   @Test
    public void testEquality(){
        assertTrue(new Dollar(5).equals(new Dollar(5)));
        assertFalse(new Dollar(5).equals(new Dollar(6)));
        assertTrue(new Franc(5).equals(new Franc(5)));
        assertFalse(new Franc(5).equals(new Franc(6)));
    }
   ```
   - 모든 Test가 잘 돌면, Franc의 equals 제거.

이처럼 한번에 옮기는게 아닌 단계적으로 공통코드를 옮긴다.
특히 불필요한 구현을 제거하기 전 두 메서드(```equals()```)의 구현을 일치시킴.

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
> * Dollar/Franc 중복
> * ~~공용 equals~~
> * 공용 times
> * **Franc과 Dollar 비교하기**
<hr/>

### 7장 사과와 오렌지
Franc과 Dollar를 비교하는 테스트를 추가해보자. (예상된 결함)
```java
assertFalse(new Franc(5).equals(new Dollar(5)));
```
실패한다. 역시 Dollar를 Franc라고 한다.
오직 금액과 클래스가 같을 때 두 Money를 같도록 반환하자.
```java
 public boolean equals(Object object){
     Money money = (Money) object;
     return amount == money.amount
             && getClass().equals(money.getClass());
 }
```
모델 코드에 클래스를 이런식으로 사용하는 것은 좀 지저분해 보인다.
하지만 통화(currency)라는 개념은 혼합된 통화간의 연산에서 필요하므로 추후 도입하자.
- 더 많은 동기가 있기전 더 많은 설계를 도입하지 말자.
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
> * Dollar/Franc 중복
> * ~~공용 equals~~
> * 공용 times
> * ~~Franc과 Dollar 비교하기~~
> * 통화?
<hr/>

### 8장 객체 만들기
두 ```times()```구현 코드가 똑같고 하위클래스가 하는 일이 많이 없는 것 같아 제거 하고싶다.
- 두 변이형 메서드 모두 Money를 반환하도록 서명부를 통일시키자.

그리고 팩토리 메서드를 사용해 하위 클래스의 직접적인 참조를 점진적으로 없애보자.
- 테스트 코드에서 콘크리트 하위 클래스의 존재를 분리하자.
> 팩토리 메서드 : `new`연산자를 이용한 객체 생성 호출을 특별한 팩토리 메서드 호출로 대체하는 것
```java
 @Test
 public void testMultiplication() {
     Money five = Money.dollar(5);
     assertEquals(new Dollar(10), five.times(2));
     assertEquals(new Dollar(15), five.times(3));
 }
```
현재 `times()`는 구현할 준비가 되지 않았기 때문에, Money를 추상클래스로 변경한 후 `times()`는 선언만 해준다.
- 최소한 메서드 선언부 만이라도 공통 상위 클래스로 옮긴다.
```java
public abstract class Money {
    ...
   // 팩토리 메서드 구현
   static Money dollar(int amount){
      return new Dollar(amount);
   }

   abstract Money times(int multiplier);
}
```
잘 실행되므로 나머지 테스트 들도 하위 클래스를 분리해주자.
- 이를 통해 어떤 클라이언트 코드도 하위클래스의 존재를 알지 못한다.
- 하위클래스의 존재를 테스트에서 분리함으로써 어떤 모델 코드에도 영향을 주지 않고 상속 구조를 마음대로 변경할 수 있다.

추가적으로 Franc의 testMultiplication의 검사하는 로직이 Dollar 곱하기 테스트랑 똑같다.
- 하위 클래스가 분리되면서, 몇몇 테스트가 불필요해 보인다.
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
> * Dollar/Franc 중복
> * ~~공용 equals~~
> * 공용 times
> * ~~Franc과 Dollar 비교하기~~
> * **통화?**
> * testFrancMultiplication을 지워야할까?
<hr/>

### 9장 우리가 사는 시간
통화를 표현하기 위해 경량 팩토리(flyweight factories)를 사용할 수 있지만, 당분간 그런 것들을 대신해 문자열을 쓰자.
```java
 @Test
 public void testCurrency(){
     assertEquals("USD", Money.dollar(1).currency());
     assertEquals("CHF", Money.franc(1).currency());
 }
```
두 클래스를 모두 포함할 수 있는 동일한 구현을 해보자.
```java
public class Dollar extends Money{
    private String currency;
    public Dollar(int amount) {
        this.amount = amount;
        currency = "USD";
    }

    Money times(int multiplier) {
        return new Dollar(amount * multiplier);
    }

    String currency() {
        // return "USD";
        return currency;
    }
}
```
처음엔 단순 "USD" String을 반환하였지만, 통화를 변수에 저장하는 방식으로 동일하게 구현하여 Money에게 curreny 변수와 메서드를 위임할 수 있게 되었다.
```java
public abstract class Money {
    ...
   protected String currency;
    ...
   String currency(){
      return currency;
   }
}
```
추가로 문자열 "USD", "CHF"를 정적 팩토리 메서드로 옮긴다면 두 생성자는 동일해지고 공통구현을 만들 수 있다.
생성자에 인자(`String currency`)를 추가한 후, 에러가 나는 코드를 고치자.
```java
public class Dollar extends Money{
    public Dollar(int amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    Money times(int multiplier) {
        // 하던 일(curreny 추가작업)이 끝나고 바꿔야하지만, 짧은 중단이므로 팩토리 메서드로 변환해준다.
        return Money.dollar(amount * multiplier);
    }
}
```
```java
public abstract class Money {
    ...
   static Money dollar(int amount){
      return new Dollar(amount, "USD");
   }

   static Money franc(int amount){
      return new Franc(amount, "CHF");
   }
   ...
   }
}
```
두 생성자가 동일해 졌으므로 상위 클래스로 올리자.
```java
public abstract class Money {
    ...
   public Money(int amount, String currency) {
      this.amount = amount;
      this.currency = currency;
   }
    ...
}
```
```java
public class Dollar extends Money {
   public Dollar(int amount, String currency) {
      super(amount, currency);
   }
   ...
}
```
정리해보면
- 다른 부분을 팩토리 메서드로 옮김으로써 두 생성자를 일치 시켰다
  - 다른부분 : `String currency`
- `times()`가 팩토리 메서드를 사용하도록 만들기 위해 리팩토링을 잠시 중단했다.
- 동일한 생성자들을 상위 클래스로 올렸다.

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
> * Dollar/Franc 중복
> * ~~공용 equals~~
> * **공용 times**
> * ~~Franc과 Dollar 비교하기~~
> * ~~통화?~~
> * testFrancMultiplication을 지워야할까?
<hr/>

### 10장 흥미로운 시간
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
> * Dollar/Franc 중복
> * ~~공용 equals~~
> * **공용 times**
> * ~~Franc과 Dollar 비교하기~~
> * ~~통화?~~
> * testFrancMultiplication을 지워야할까?

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