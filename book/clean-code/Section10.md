# 10장 클래스
### 클래스 체계
클래스를 정의하는 표준 자바 관례에 따르면
1. static public 상수
2. static private 변수
3. private 변수
   - public 변수가 필요한 경우는 거의 없음
4. public 함수
5. private 함수 (자신을 호출하는 공개함수 직후에 넣음)

> 추상화 단계는 순차적으로 내려간다.

#### 캡슐화
- 변수와 유틸리티 함수는 가능한 공개하지 않는 편이 낫지만 반드시 숨겨야 한단는 법칙도 없다.
- 같은 패키지 안에서 테스트 코드가 함수를 호출하거나 변수를 사용해야 한다면, `protected`로 선언하거나 패키지 전체로 공개하여 접근을 허용하기도 한다.
- **하지만 캡슐화를 풀어주는 결정은 최후의 수단이다.**

### 클래스는 작아야 한다!
클래스를 설계할 때도 함수와 마찬가지로 `작게`가 기본규칙이다. 함수는 물리적인 행 수로 크기를 측정했다면, 클래스는 맡은 **책임**으로 센다.

클래스 이름은 해당 클래스 책임을 기술해야한다.
- 작명은 클래스 크기를 줄이는 첫 번째 관문이다.
- 간결한 이름이 떠오르지 않는다면 클래스의 책임이 너무 많아서이다.
- Processor, Manager, Super등과 같이 모호한 단어가 있다면 클래스에 여러 책임을 떠안겼다는 증거이다.

#### 단일 책임 원칙(Single Responsibility Principle)
단일 책임 원칙 : 클래스나 모듈은 하나의 책임을 가져야한다. 즉, 변경할 이유가 하나여야 한다.

```java
public class SuperDashboard extends JFrame implements MetaDataUser
    public Component getLastFocusedComponent()
    public void setLastFocused(Component lastFocused)
    public int getMajorVersionNumber()
    public int getMinorVersionNumber()
    public int getBuildNumber()
}
```

`SuperDashboard` 클래스는 작어보이지만, 변경할 이유가 2가지 이다.
1. 소프트웨어 버전 정보를 추적한다. ➡️ 버전은 소프트웨어를 출시할 때마다 달라진다.
2. 자바 스윙컴포넌트를 관리한다. ➡️ 스윙코드 변경 시 버전 번호도 함께 달라진다.

따라서 `Version`이라는 독자적 클래스를 만들어 버전에 대한 책임을 분리할 수 있다.
```java
public class Version {
   public int getMajorVersionNumber()
   public int getMinorVersionNumber()
   public int getBuildNumber()
}
```

SRP는 이해하고 지키기 수월한 개념이지만 설계자가 가장 무시하는 규칙 중 하나다. 

일반적으로 소프트웨어를 돌아가게 만드는 활동 후 깨끗하고 체계적으로 만드는 활동으로 넘어가지 않기 때문에 만능 클래스로 둔다.
물론 자잘한 클래스가 많아지면 이해하기 힘들다 여겨질 수 있지만, 클래스가 적든 많든 시스템에 익힐 내용의 양은 비슷하다.
> *도구 상자를 어떻게 관리하고 싶은가? 작은 서랍을 두고 기능과 이름이 명확한 컴포넌트를 나눠 넣고싶은가? 아니면 큰 서랍 몇 개를 두고 모두를 던져넣고 싶은가?*

따라서 규모가 큰 시스템일 수록 복잡성을 다루려면 체계적인 정리가 필수이다. 만약 책임이 잘 나누어져 있다면, 변경을 가할 때 **개발자는 직접 영향을 미치는 컴포넌트만 이해해도 충분하다.** 

#### 응집도(Cohesion)
클래스는 인스턴스 변수 수가 작아야 하며, 각 클래스 메서드는 클래스 인스턴스 변수를 하나 이상 사용해야한다.
- 일반적으로 메서드가 인스턴스 변수를 많이 사용할 수록 모두 사용할 수록 클래스 응집도가 높다.
- 응집도가 높다는 말은 **클래스에 속한 메서드와 변수가 서로 의존하며 논리적 단위로 묶인다**는 의미이기 때문에 이를 선호한다.

`함수를 작게, 매개변수 목록을 짧게`라는 전략을 따르다보면, 때때로 몇몇 메서드만이 사용하는 인스턴스 변수가 많아지는데 이는 새로운 클래스로 쪼개야한다는 신호이다.
응집도가 높아지도록 변수와 메서드를 적절히 분리해 새로운 클래스로 쪼개주자.

#### 응집도를 유지하면 작은 클래스 여럿이 나온다.
- 큰 함수를 작은 함수로 나누기만해도 클래스 수가 많아진다.
- 만약 빼내어 새로 정의하려는 코드가 큰 함수에 정의된 변수 4개를 사용한다면 이를 인수로 넘기는게 아니라 **인스턴스로 넘긴다.**
- 즉 4개의 변수로 이루어진 새로운 클래스를 만든다면, 새 함수의 인수를 줄일 수 있다.

큰 함수를 작은 함수로 여럿 쪼개다 보면 프로그램은 점점 체계가 잡히고 구조가 투명해진다.





