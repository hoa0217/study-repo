## 2. 자바와 절차적/구조적 프로그래밍

### 자바 프로그램의 개발과 구동
|현실세계         |가상 세계(자바 월드) ||
|---------|----------|----------|
|소프트웨어 개발 도구|JDK(Java Development Kit) - 자바 개발 도구|JVM용 소프트웨어 개발 도구|
|운영체제|JRE(Java Runtime Environment) - 자바 실행 환경|JVM용 OS|
|하드웨어 - 물리적 컴퓨터|JVM(Java Virtual Machine) - 자바 가상 기계|가상의 컴퓨터|
- JDK를 이용해 개발된 프로그램은 JRE에 의해 가상의 컴퓨터인 JVM상에서 구동된다.
  
    <img src="img/jvm_jre_jdk2.jpeg" width="70%"/>
  
    - JDK가 JRE를 포함하고 JRE는 JVM을 포함하는 형태로 배포된다.
    - 이로인해 개발자는 본인 OS에 맞는 JVM용으로 프로그램을 작성하고 배포하면 JVM이 중재자로서 각 OS에서 구동하는 데 아무 문제 없이 만들어준다.

- 객체 지향 프로그램의 메모리 사용방식

  <img src="img/t_memory.jpeg" width="70%"/>
  
  - 데이터 저장 영역 = T메모리 구조

<hr/>

### 자바에 존재하는 절차적/구조적 프로그래밍의 유산
- 절차적 프로그래밍 : **goto**를 쓰지 말라는 것
  - goto : 프로그램의 실행 순서를 이리저리 이동 시키는 예약어 (제어의 역할)
  - 이동이 잦아지면 프로그램 이해가 힘들다. 프로그램을 논리적으로 잘 구성하면 goto를 피할 수 있다.
- 구조적 프로그래밍 : **함수**를 쓰라는 것
  - 중복코드를 한곳에 모아 관리할 수 있고 논리를 함수 단위로 분리해서 이해하기 쉬운 코드를 작성할 수 있다.
  > 공유 문제로 전역변수보다 지역변수를 쓰라는 지침도 있음.
- 객체 지향 언어에서 절차적/구조적 프로그래밍의 유산은 메서드 안에서 확인할 수 있다.
  - **제어문**이 존재할 수 있는 공간은 바로 **메서드 내부**이기 때문.
  - goto = 제어문 , 함수 = 메서드
  > 함수와 메서드 차이 : 함수는 클래스나 객체와 아무관계 없지만 메서드는 반드시 클래스 안에 존재해야 한다.
- 절차적/구조적 프로그래밍의 유산의 예를 보면 자바 키워드 중 절반 이상이다.
  - for, switch, if, else, while, return, continue, break 등
- 객체지향을 이해하기위해 절차적/구조적 프로그래밍을 이해해보자.

<hr/>

### 다시 보는 main()메서드 : 메서드 스택 프레임
```java
ackage section2.ex2_1;

public class Start{

  public static void main(String[] args) {
    System.out.println("Hello OOP!!");
  }
}
```
1. JRE는 프로그램 안에 `main()`메서드가 있는지 확인한다.
2. 확인 되면 JRE는 JVM에 전원을 넣어 부팅한다.
3. 부팅된 JVM은 목적 파일을 실행한다. (메모리 구조를 구성 및 구문 실행)   
   3-1. `main()`메서드 실행전 **전처리** 작업을 진행한다.
   
   - 가장 먼저 `java.lang` 패키지를 T메모리 스태틱 영역에 배치한다.
   - 개발자가 작성한 모든 클래스와 `import패키지` 역시 T메모리 스태틱 영역에 배치한다.
  
   3-2. `main()`메서드를 스택프레임(stack frame)에 할당한다.   
   
   - 여는 중괄호를 만날 때 스팩프레임이 하나씩 생기며 닫는 중괄호를 만나면 스택프레임이 소멸된다.
  
   3-3. 메서드 인자인 `args`를 스택프레임(stack frame)의 변수 공간에 할당한다.    
   3-4. 메모리 구조 구성 후 `main()`메서드의 첫 명령문을 실행한다.
   
   <img src="img/run.jpeg" width="70%"/>
   
   3-5. 구문 실행이 끝나면 `main()`메서드의 스택프레임이 소멸된다.
4. 실행이 끝나면 JRE는 JVM을 종료한다.
5. JRE도 사용했던 자원을 운영체제에 반납한다. (운영체제 상의 메모리에서 소멸)

- 실제 Debug 화면
  
    <img src="img/debug.png" />

<hr/>

### 변수와 메모리 : 변수! 너 어디 있니?

```java
package section2.ex2_2;

public class Start2 {

  public static void main(String[] args) {
    int i;
    i = 10;

    double d = 20.0;
  }
}
```
- `int i;`
    - 메모리에 4바이트 크기의 정수 저장 공간을 main()메서드 스택프레임안에 마련하라는 명령어다.
    - 아직 초기화 되지 않은 상태이므로, 다른 프로그램이 청소하지 않고 간 값을 그대로 가지고 있게 된다.
    - 이 상태로 i 변수를 사용하는 코드를 만나게 되면, 컴파일 에러가 발생한다.
        - `The local variable i may not have been initialized`
    
    <img src="img/stack1.jpeg" width="60%"/>
  
- `i = 10;` `double d = 20.0;`
    - `double d = 20.0;`의 경우 변수 선언 명령문과 할당 명령문이 한줄에 있는 것

    <img src="img/stack2.jpeg" width="60%"/>
    
<hr/>

### 블록 구문과 메모리 : 블록 스택 프레임

```java
package section2.ex2_3;

public class Start3 {

  public static void main(String[] args) {
    int i = 10;
    int k = 20;

    if (i == 10) {
      int m = k + 5;
      k = m;
    } else {
      int p = k + 10;
      k = p;
    }
  }

  //k = m + p;
}
```

- `if (i == 10)` 분기
    - 비교 결과가 true 이므로 if블록의 스택 프레임이 main() 메서드의 스택프레임 안에 **중첩**되어 생성된다.
- `int m = k + 5;`
    - 변수 m에 값을 할당한다. 
    - 이 때 if 스택 프레임 밖에 있는 k 변수를 연산에 참여시킨다.
      
    <img src="img/stack3.jpeg" width="60%"/>

- if 블록 종료 후 스택 프레임은 소멸된다.
    - else 블록은 스택 메모리에 등장하지도 못했음.

    <img src="img/stack4.jpeg" width="60%"/>

> 만약 주석을 해제한다면?   
> m변수와 p변수는 더 이상 존재하지 않는다. 주석을 풀고 실행하면 컴파일 오류가 발생한다.   
> `m cannot be resolved to a variable` , `p cannot be resolved to a variable`
    
<hr/>

### 지역 변수와 메모리 : 스택 프레임에 갇혔어요!
    

