# 13장 동시성
동시성과 깔끔한 코드는 양립하기 어렵다. 스레드 하나만 실행하는 코드는 짜기 쉬우나, 다중 스레드는 어렵다.

### 동시성이 필요한 이유?
- 동시성은 결합(coupling)을 없애는 전략이다. 즉, 무엇(what)과 언제(when)를 분리하는 전략이다.
- 스레드가 하나인 프로그램은 무엇과 언제가 밀접하여, 호출 스택을 살펴보면 프로그램 상태가 곧바로 드러난다.
- 무엇과 언제를 분리하면 애플리케이션 구조와 효율이 극적으로 나아진다.
- 구조적 관점에서 프로그램이 하나의 거대한 루프가 아닌, 작은 협력 프로그램 여럿으로 보인다.
- 따라서 시스템을 이해하기가 쉽고 문제를 분리하기도 쉽다.

예시
- 매일 수많은 웹사이트 정보를 가져와 요약하는 정보 수집기를 생각해보자.
- 만약 수집기가 단일 스레드 프로그램이라면 한번에 한 웹사이트를 방문해 정보를 가져오며, 한 사이트를 끝내야 다음 사이트로 넘어간다.
- 하지만 다중 스레드 알고리즘을 이용하면 동시에 여러 웹사이트를 방문할 수 있으므로 수집기 성능을 높일 수 있다.

#### 미신과 오해
동시성은 각별히 주의하지 않으면 난감한 상황에 처한다. 다음은 동시성과 관련한 일반적인 미신과 오해다.

- 동시성은 항상 성능을 높여준다.
  - 동시성은 때로 성능을 높여준다. 
  - 대기 시간이 아주 길어 여러 스레드가 프로세서를 공유할 수 있거나
  - 여러 프로세서가 동시에 처리할 독립적인 계산이 충분히 많은 경우에만 성능이 높아진다.
  - 어느쪽도 일반적인 상황은 아니다.
- 동시성은 구현해도 설계는 변하지 않는다.
  - 단일 스레드와 다중 스레드 시스템은 설계가 판이하게 다르다.
- 웹 또는 EJB 컨테이너를 사용하면 동시성을 이해할 필요가 없다.
  - **실제로 컨테이너가 어떻게 동작하는지, 어떻게 동시 수정, 데드락 등과 같은 문제를 피할 수 있는지 알아야만 한다.**

아래는 타당한 생각 몇가지다.

- 동시성은 다소 부하를 유발한다. 성능 측면에서 부하가 걸리며, 코드도 더 짜야한다.
- 동시성은 복잡하다. 간단한 문제라도 동시성은 복잡하다.
- 일반적으로 동시성 버그는 재현하기 어렵다. 그래서 진짜 결함으로 간주되지 않고 일회성 문제로 여겨 무시하기 쉽다.
- 동시성을 구현하려면 흔히 **근본적인 설계 전략**을 재고해야한다.

### 난관
```java
public class X {
    private int lastIdUsed;
    
    public int getNextId(){
        return ++lastIdUsed;
    }
}
```
인스턴스 X를 생성하고 필드를 42로 설정한다. 그리고 두 스레드가 해당 인스턴스를 공유하며 `getNextId()`를 호출한다면?
- 한 스레드는 43을 받는다. 다른 스레드는 44를 받는다. lastIdUsed는 44가된다.
- 한 스레드는 44를 반든다. 다른 스레드는 43을 받는다. lastIdUsed는 44가된다.
- 한 스레드는 43을 받는다. 다른 스레드는 43을 받는다. lastIdUsed는 43이된다. (동시에 호출)

대다수는 올바른 결과를 내놓지만, 동시에 같은 변수를 동시에 참조하는 등 **일부 잘못된 결과**를 내놓는다.

### 동시성 방어 원칙
#### 단일 책임 원칙(Single Responsibility Principle, SRP)
주어진 메서드/클래스/컴포넌트를 변경할 이유가 하나여야 한다는 원칙이다.
**즉, 동시성은 복잡성 하나만으로도 다른코드와 분리해야한다.**

동시성을 구현할 때 다음 몇가지를 고려한다.
- 동시성 코드는 독자적인 개발, 변경, 조율 주기가 있다.
- 동시성 코드에는 독자적인 난관이 있다. (다른 난관과 다르며 훨씬 어려움)
- 잘못 구현한 동시성 코드는 별의별 방식으로 실패한다. (다른 코드가 발목을 잡지 않더라도, 동시성만으로 충분히 어려움)

⭐️ 권장사항 : 동시성 코드는 다른 코드와 분리하라.

#### 따름 정리(corollary): 자료 범위를 제한하라
두 스레드가 서로 간섭하지 못하도록, 공유 객체를 사용하는 코드 내 **임계영역(critical section)을 `synchronized` 키워드로 보호**한다.

또한 이런 **임계영역의 수를 줄이는 기술**도 중요하다. 공유 자료를 수정하는 위치가 많을수록 아래 가능성도 커진다.
- 보호할 임계영역을 빼먹는다.
- 모든 임계영역을 보호했는지(DRY 위반)확인하느라 똑같은 노력을 반복한다.
- 안그래도 찾기 어려운 버그는 더 찾기 어려워진다.

⭐️ 권장사항 : 자료를 캡슐화(encapsulation)하라. 공유 자료를 최대한 줄여라.

#### 따름 정리(corollary): 자료 사본을 사용하라
공유 자료를 줄이려면, 처음부터 공유하지 않는 방법이 제일 좋다.
- 객체를 복사해 읽기 전용으로 사용하는 방법
- 각 스레드가 객체를 복사해 사용한 후, 한 스레드가 사본에서 결과를 가져오는 방법

Lock으로 자료 접근을 관리하는 것 보다, 사본 생성 후 가비지 컬렉션 수행하는게 더 효율적일 가능성이 크다.

#### 따름 정리(corollary): 스레드는 가능한 독립적으로 구현하라
스레드는 자료를 공유하지 않고 모든 정보를 로컬 변수에 저장하면 **다른 스레드와 동기화할 필요가 없으므로 자신만 있는 듯이 돌아갈 수 있다.**

⭐️ 권장사항 : 독자적인 스레드로, 가능하면 다른 프로세서에서 돌려도 괜찮도록 자료를 독립적 단위로 분할하라.

### 라이브러리를 이해하라
자바5로 스레드 코드를 구현한다면 다음을 고려해야한다.
- 스레드 환경에 안전한 컬렉션을 사용한다.
- 서로 무관한 작업을 수행할 때는 executor 프레임워크를 사용한다.
- 가능하다면 스레드가 차단되지 않는 방법을 사용한다.
- 일부 클래스 라이브러리는 스레드에 안전하지 못하다.

#### 스레드 환경에 안전한 컬렉션
`java.util.concurrent` 패키지가 제공하는 클래스는 다중 스레드 환경에서 사용해도 안전하며, 성능도 좋다.

복잡한 동시성 설계를 지원하는 클래스도 Java5에 추가되었다.
- ReentrantLock : 한 메서드에서 잠그고 다른 메서드에서 푸는 락(lock)
- Semaphore : 전형적인 세마포다. 개수(count)가 있는 락이다.
- CountDownLatch : 지정하는 수 만큼 이벤트가 발생하고 나서야 대기중인 스레드를 모두 해제하는 락이다.
  - 모든 스레드에게 동시에 공평하게 시작할 기회를 준다.
  
⭐️ 권장사항 : `java.util.concurrent`, `java.util.concurrent.atomic`, `java.util.concurrent.lock` 패키지가 제공하는 클래스를 익혀라.

### 실행 모델을 이해하라
다중 스레드 애플리케이션을 분류하는 방식은 여러가지다. 그전에 몇가지 용어를 이해하자.
- 한정된 자원(Bound Resource) : 다중 스레드 환경에서 사용하는 자원으로, 크기나 숫자가 제한적이다. 
  - 데이터베이스 연결, 길이가 일정한 읽기/쓰기 버퍼 등이 예다.
- 상호 배제(Mutual Exclusion) : 한번에 한 스레드만 공유 자료나 공유 자원을 사용할 수 있는 경우를 가리킨다.
- 기아(Starvation) : 한 스레드나 여러 스레드가 굉장히 오랫동안 혹은 영원히 자원을 기다린다.
  - 예를 들어, 항상 짧은 스레드에게 우선순위를 준다면 짧은 스레드가 지속적으로 이어질 경우 긴스레드는 기아 상태에 빠진다.
- 데드락(Deadlock) : 여러 스레드가 서로가 끝나기를 기다린다. 
  - 모든 스레드가 각기 필요한 자원을 다른 스레드가 점유하는 바람에 어느쪽도 진행하지 못한다.
- 라이브락(Livelock) : 락을 거는 단계에서 각 스레드가 서로를 방해한다.
  - 스레드는 계속해서 진행하려 하지만, 공명(resonance)로 인해 굉장히 오랫동안 혹은 영원히 진행하지 못한다. 

#### 1. 생산자-소비자(Producer-Consumer)
하나 이상 생산자 스레드가 정보를 생성해 대기열에 넣고, 하나 이상 소비자 스레드는 대기열에서 정보를 가져와 사용한다.

이때 대기열은 **한정된 자원**이다.
- 생산자 스레드는 대기열에 빈칸이 있어야 정보를 채우기에, 빈공간이 생길때까지 기다린다.
- 소비자 스레드는 대기열에 정보가 있어야 가져오기에, 정보가 채워질때까지 기다린다.

대기열을 올바르게 사용하고자 **생산자 스레드와 소비자 스레드는 서로에게 시그널**을 보낸다.
- 생산자 스레드는 정보를 채우고 "정보가 있다"는 시그널을 보낸다.
- 소비자 스레드는 대기열에 정보를 읽은 후 "빈공간이 있다"는 시그널을 보낸다.

따라서 잘못하면 두 스레드 다 진행이 가능함에도 **동시에 서로에게 시그널을 기다릴 가능성이 존재한다.**

#### 2. 읽기-쓰기(Readers-Writers)
- 읽기 스레드 : 주된 정보원으로 공유 자원을 사용 (버퍼 점유시간 짧음)
- 쓰기 스레드 : 이따끔 공유자원을 갱신 (버퍼 점유시간 김)

만약 읽기 스레드에게 우선권을 준다면?
- 읽기 스레드가 없을 때까지 쓰기 스레드는 버퍼에서 기다린다. 
- 처리율(throughput)은 높아져도 쓰기 스레드가 기아(starvation)가 될 가능성이 있다.

만약 쓰기 스레드에게 우선권을 준다면?
- 쓰기 스레드가 버퍼를 오래 점유하게되어 여러 읽기 스레드가 버퍼를 기다린다.
- 기아(starvation)는 방지해도 처리율(throughput)이 떨어진다.

이 경우 읽기 스레드와 쓰기 스레드의 요구를 적절히 만족시켜 처리율도 적당히 높이고 기아도 방지하는 해법이 필요하다.

#### 3. 식사하는 철학자들(Dining Philosophers)

![image](https://github.com/f-lab-edu/modoospace/assets/48192141/4ec960bc-aa6f-4d51-b02b-dd6fd03316c6)

- 철학자들은 배가 고프지 않으면 생각을 배가 고프면 양손에 포크를 집어들고 스파게티를 먹는다.
- 양손에 포크를 쥐지 못하면 먹지 못하는데, 왼쪽 철학자나 오른쪽 철학자가 포크를 사용중이라면 끝날때까지 기다려야한다.
- 여기서 포크를 자원으로 생각해보면 여러 프로세스가 자원을 얻으려 경쟁하는 상황이된다.
- 즉, 주의해서 설계하지 않으면 데드락, 라이브락, 처리율 저하, 효율성 저하 등을 겪을 수 있다.

#### 결론
일상에서 접하는 대다수 다중 스레드 문제는 위 세 범주 중 하나에 속한다.
따라서 알고리즘을 공부하고 해법을 직접 구현해보면 실전에 부닥쳤을 때 해결이 쉬워지리라..!

### 동기화하는 메서드 사이에 존재하는 의존성을 이해하라

자바에는 개별 메서드를 보호하는 `synchronized`라는 개념을 지원한다. 만약 동기화하는 메서드 사이에 의존성이 존재할 경우 찾아내기 어려운 버그가 생길 수 있다.

⭐️ 권장사항 : 공유 객체 하나에는 메서드 하나만 사용하라.

하지만 공유 객체 하나에 여러 메서드가 필요한 상황도 생긴다. 이때 아래 3가지 방법을 고려한다.
- 클라이언트에서 잠금 : 클라이언트에서 첫 번째 메서드를 호출하기 전에 서버를 잠근다. 마지막 메서드를 호출할 때 까지 잠금을 유지한다.
- 서버에서 잠금 : 서버에다 "서버를 잠그고 모든 메서드를 호출한 후 잠금을 해제하는" 메서드를 구현한다. 클라이언트는 이 메서드를 호출한다.
- 연결(Adapted)서버 : 잠금을 수행하는 중간 단계를 생성한다. `서버에서 잠금`방식과 유사하지만 원래 서버는 변경하지 않는다.

### 동기화하는 부분을 작게 만들어라

자바에서 `synchronized` 키워드를 사용하면 락을 설정한다. 락으로 감싼 모든 코드 영역은 한번에 한 스레드만 실행 가능하므로 스레드를 지연시키고 부하를 가중시킬 수 있다.
따라서 여기저기 `synchronized`를 남발하는 코드는 바람직하지 않다.

반면 임계영역은 반드시 보호해야한다. 따라서 코드를 짤 때 임계영역 수를 최대한 줄여야한다. 
그런데 수를 줄이고자 크기를 필요이상으로 늘려버리면 스레드간 경쟁이 늘어나고 프로그램 성능이 떨어지므로 주의해야한다.

⭐️ 권장사항 : 동기화하는 부분을 최대한 작게 만들어라.

### 올바른 종료 코드는 구현하기 어렵다.
영구적으로 돌아가는 시스템과 잠시 돌다 깔끔하게 종료하는 시스템을 구현하는 방법은 다르다.

깔끔하게 종료하는 코드는 스레드가 절대 오지 않을 시그널을 기다리는 **데드락 문제**가 발생할 수 있다.
- 만약 부모 스레드가 자식 스레드를 여러 개 만든 후 끝나기를 기다렸다 종료하는 시스템이 있다고 가정하자.
- 이때 자식스레드 중 하나가 데드락에 걸렸다면 부모스레드는 영원히 기다리고 시스템은 영원히 종료하지 못한다.

따라서 해당 코드를 짜야한다면 시간을 투자해 올바르게 구현해야한다.

### 스레드 코드 테스트하기

⭐️ 권장사항 : 문제를 노출하는 테스트 케이스를 작성하라. 프로그램 설정과 시스템 설정과 부하를 바꿔가며 자주 돌려라. 테스트가 실패하면 원인을 추척하라. 다시 돌렸더니 통과하더라는 이유로 그냥 넘어가면 절대 안된다.

고려사항이 아주 많다는 뜻이며, 아래 구체적인 지침을 제시한다.

- 말이 안되는 실패는 잠정적인 스레드 문제로 취급하라.
  - 시스템 실패를 '일회성'이라 치부하지마라. 이를 무시한다면 잘못된 코드위에 코드가 계속 쌓인다. 
- 다중 스레드를 고려하지 않은 순차 코드부터 제대로 돌게 만들자.
  - 먼저 스레드 환경 밖에서 코드가 도는지 확인한다. 일반적 방법으로 스레드가 호출하는 POJO를 만든다.
  - 스레드 환경 밖에서 생기는 버그와 스레드 환경에서 생기는 버그를 동시에 디버깅 하지마라. 
- 다중 스레드를 쓰는 코드 부분을 다양한 환경에 쉽게 끼워 넣을 수 있도록 스레드 코드를 구현하라.
  - 한 스레드로 실행하거나, 여러 스레드로 실행하거나, 실행 중 스레드 수를 바꿔본다.
  - 스레드 코드를 실제 환경이나 테스트 환경에서 돌려본다.
  - 테스트 코드를 빨리, 천천히, 다양한 속도로 돌려본다.
  - 반복 테스트가 가능하도록 테스트 케이스를 작성한다.
- 다중 스레드를 쓰는 코드 부분을 상황에 맞춰 조정할 수 있게 작성하라.
  - 적절한 스레드 개수를 파악하려면 상당한 시행착오가 필요하므로, 스레드 개수를 조율하기 쉽게 코드를 구현한다. 
- 프로세서 수보다 많은 스레드를 돌려보라.
  - 시스템이 스레드를 스와핑할 때도 문제가 발생하므로, 스와핑을 일으키려면 프로세스 수 보다 많은 스레드를 돌린다.
  - 스와핑이 잦을수록 임계영역을 빼먹은 코드나 데드락을 일으키는 코드를 찾기 쉬워진다.
- 다른 플랫폼에서 돌려보라.
  - 운영체제마다 스레드를 처리하는 정책이 다르므로, 코드가 돌아갈 가능성이 있는 플랫폼 전부 테스트를 수행해야한다.  
- 코드에 보조 코드(instrument)를 넣어 돌려라. 강제로 실패를 일으키게 해보라.
  - 코드가 실행되는 수천 가지 경로 중 아주 소수만 실패 기때문에, 스레드 코드에서 오류를 찾기 쉽지않다.
  - 따라서 보조 코드를 추가해 **코드를 실행하는 순서를 바꿔준다.**   
    ➡️ 스레드 실행 순서에 영향을 미치기 때문에 버그가 드러날 가능성이 높아진다.    
    ex) `Object.wait()`, `Object.sleep()`, `Object.yeild()`, `Object.priority()` 등 

---

### 결론
간단한 코드도 여러 스레드와 공유자료를 추가하면 복잡해진다. 따라서 다중 스레드 코드를 작성한다면 아래 주의사항을 지키며 각별히 깨끗하게 코드를 짜야한다.

- SRP를 준수한다.
  - 스레드를 아는 코드와 스레드를 모르는 코드를 분리한다.
  - 스레드 코드를 테스트할 때는 전적으로 스레드만 테스트한다.
  - 따라서 스레드 코드는 최대한 집약되고 작아야한다.
- 동시성 오류를 일으키는 잠정적 원인을 철저히 이해한다.
  - 여러 스레드가 공유 자료를 조작하거나 공유할 때 동시성 오류가 발생한다.
  - 프로그램을 깔끔하게 종료하는 등 경계조건의 경우 특히 주의해야한다.
- 사용하는 라이브러리와 기본 알고리즘을 이해한다.
  - 특정 라이브러리 기능이 기본 알고리즘과 유사한 어떤 문제를 어떻게 해결하는지 파악한다.
- 보호할 코드 영역을 찾아내는 방법과 특정 코드 영역을 잠그는 방법을 이해한다.
  - 공유하는 정보와 공유하지 않는 정보를 제대로 이해하고 공유하는 객체 수와 범위를 최대한 줄인다.
- 어떻게든 문제는 생기므로 이를 일회성으로 치부하면 안된다. 
  - 스레드 코드는 많은 플랫폼에서 많은 설정으로 반복해서 계속 테스트해야한다.
  - 시간을 들여 보조 코드를 추가하면 오류가 드러날 가능성이 높아진다.
  - 출시하기 전까지 최대한 오랫동안 돌려본다.

깔끔한 접근 방식을 취한다면, 코드가 올바로 돌아갈 가능성이 극적으로 높아진다.
