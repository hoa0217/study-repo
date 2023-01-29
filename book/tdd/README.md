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

### Money 객체 예제
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

연산을 수행한 후 해당 Dollar의 값이 바뀐다. 이러한 부작용을 해결하기위해 times()가 객체를 반환하게 해보자.
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
        //return null;
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


    
    
   
   
   
   
