# 4장 주석
주석은 오래될수록 코드에서 멀어지며 오래될수록 그릇될 가능성도 커진다.
- 프로그래머들이 주석을 유지하고 보수하기란 불가능하다.
- 하지만 코드는 변화하고 진화한다.

코드만이 정확한 정보를 제공하는 유일한 출처이다.
**따라서 우리는 주석을 가능한 줄이도록 꾸준히 노력해야한다.**

---

### 주석은 나쁜 코드를 보완하지 못한다.
- 코드에 주석을 추가하는 이유는 코드 품질이 나쁘기 때문이다.
- **자신이 저지른 난장판을 주석으로 설명하려 애쓰는 대신 그 난장판을 깨끗이 치우는데 시간을 보내라**

### 코드로 의도를 표현하라!
- 코드만으로 의도를 표현하기 어렵다면?
```java
// 직원에게 복지 혜택을 받을 자격이 있는지 검사한다.
if ((employee.flags & HOURLY_FLAG) && (employee.age > 65))
```
- 주석으로 달려는 설명을 함수로 만들어 표현해도 충분하다.
```java
if (employee.isEligibleForFullBenefits())
```

### 좋은 주석
#### 법적인 주석
- 소스파일 첫머리에 주석으로 들어가는 저작권 정보와 소유권 정보는 필요하고도 타당하다.

#### 정보를 제공하는 주석
- 기본적인 정보를 주석으로 제공하면 편리하다.
```java
// kk:mm:ss EEE, MMM dd, yyyy 형식이다.
Pattern timeMatcher = Patter.complie("\\d*:\\d*:\\d* \\w*, \\w* \\d*, \\d*"); 
```
- 하지만 가능하다면 **함수 이름**에 정보를 담는 편이 좋다.
- 또는 기본적으로 제공해주는 UtilClass의 기능을 활용하면 주석이 필요없어진다.
```java
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm:ss EEE, MMM dd, yyyy");
```

#### 의도를 설명하는 주석
- 개발자의 결정에 깔린 의도를 설명하는 주석
```java
public int compareTo(Object o){
	if(o instanceof WikiPagePath){
		...
	}
	return 1; // 오른쪽 유형이므로 정렬 순위가 더 높다.
}
```

#### 의미를 명료하게 밝히는 주석
- 모호한 인수나 반환값이 표준 라이브러리나 변경하지 못하는 코드에 속한다면 의미를 명료하게 밝히는 주석이 유용하다.
```java
public void testCompareTo() throws Exception {
	...
  assertTrue(a.compareTo(a) == 0); // a == a
  assertTrue(a.compareTo(b) != 0); // a != b
  assertTrue(a.compareTo(b) == -1); // a < b
  assertTrue(b.compareTo(a) == 1); // b > a
}
```

> 하지만, 주석이 올바른지 검증하기 쉽지않으므로 주석을 달 때 더 나은 방법이 없는지 고민하고 정확히 달도록 주의한다.


#### 결과를 경고하는 주석
- 다른 개발자에게 결과를 경고할 목적으로 사용하는 주석
```java
public static SimpleDateFormat makeStandardHttpDateFormat() {
    // SimpleDateFormat은 스레드에 안전하지 못하다
    // 따라서 각 인스턴스를 독립적으로 생성해야한다.
    SimpleDateFormat df = new SimpleDateFormat("kk:mm:ss EEE, MMM dd, yyyy");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    return df;
}
```

#### TODO 주석
- ‘앞으로 할일’을 `//TODO` 남겨두면 편하다.
- 이는 필요하다 여기지만 당장 구현하기 어려운 업무를 기술한다.
- 그래도 TODO로 떡칠한 코드는 바람직하지않으므로 주기적으로 점검해 없애라고 권한다.

#### 중요성을 강조하는 주석
- 자칫 대수롭지 않다고 여겨질 뭔가의 중요성을 강조하기 위해 주석을 사용한다.
```java
String listItemContent = match.group(3).trim()
// 여기서 trim은 정말 중요하다.trim 함수는 문자열에서 시작 공백을 제거한다
// 문자열에 시작 공백이 잇으면 다른 문자열로 인식되기 때문이다.
```

#### 공개API에서 Javadocs
- 공개 API를 구현한다면 반드시 훌륭한 Javadocs를 작성한다.

### 나쁜 주석
#### 주절저리는 주석
- 주석을 달기로 결정했다면 충분한 시간을 들여 최고의 주석을 달도록 노력해야한다.
- 만약 이해가 안되어 다른 모듈까지 뒤져야 하는 주석은 독자와 제대로 소통하지 못하는 주석이다.

#### 같은 이야기를 중복하는 주석
- 코드 내용을 그대로 중복하는 주석은 실제 코드보다 부정확해 독자가 대충 이해하고 넘어가게 만든다.
```java
// this.closed가 true일 때 반환되는 유틸리티 메서드다.
// 타임아웃에 도달하면 예외를 던진다.
public synchronized void waitForClose(final long timeoutMillis) throws Exception {
	if(!closed) {
		wait(timeoutMillis);
		if(!closed)
			throw new Exception();
	}
}
```

#### 오해할 여지가 있는 주석
- 위 예제는 중복도 많으면서 오해할 여지도 존재한다.
    - `this.closed`가 `true`일 때 그 무엇도 반환하지 않는다.
- 주석에 담긴 잘못된 정보로 인해 프로그래머는 이유를 찾느라 골머리는 앓는다.

#### 의무적으로 다는 주석
- 모든 함수에 Javadocs를 달거나 모든 변수에 주석을 달아야 한다는 규칙은 코드를 복잡하게 만들며, 거짓말을 퍼뜨리고 혼동과 무질서를 초래한다.

#### 이력을 기록하는 주석
- 예전엔 모든 모듈 첫머리에 변경 이력을 기록하는 관계가 바람직했다.
- 하지만 당시엔 소스코드관리시스템(형상관리시스템)이 없었으니까 이제는 혼란만 가중할 뿐이다.

#### 있으나 마나 한 주석
- 너무 당연한 사실을 언급하며 새로운 정보를 제공하지 못하는 주석
```java
/**
 * 기본 생성자
 */
protected AnnualDateRule(){}
```

> 지나친 참견은 주석을 무시하게 만든다.

#### 무서운 잡음

- 수행하는 목적없이 제공되는 주석은 잡음일 뿐이다.

```java
/** The version. **/
private String version;

/** The version. **/
private String info; 
```

> copy & paste 오류 같아 보임.

#### 함수나 변수로 표현할 수 있다면 주석을 달지 마라
- 주석이 필요하지 않도록 코드를 개선하자.

```java
// 전역 목록 <smodule>에 속하는 모듈이 우리가 속한 하위 시스템에 의존하는가?
if (smodule.getDependSubsystems().contains(subSysMod.getSubSystem()))
```

```java
ArrayList moduleDependees = smodule.getDependSubsystems();
String ourSubSystem = subSysMod.getSubSystem();
if(moduleDependees.contains(ourSubSystem))
```

#### 위치를 표시하는 주석
- 때때로 소스파일에서 특정 위치를 표시하려 주석을 사용한다.

```java
// Actions /////////////////////
```

- 배너 아래 특정 기능을 모아놓으면 유용한 경우도 있으며, 이는 눈에 띄므로 주의를 환기한다.
- 하지만 이는 가독성을 낮추며 남용하면 독자가 흔한 잡음으로 여겨 무시할 수 있으므로
- **반드시 필요할 때만, 아주 드물게 사용하는 편이 좋다.**

#### 닫는 괄호에 다는 주석
- 중첩이 심하고 장황한 함수라면 이 주석은 의미가 있을 지 모르지만, 작고 캡슐화된 함수에는 잡음일 뿐이다.
- **주석을 달아야겠다는 생각이 든다면 함수를 줄이려 시도하자.**

```java
try {
	while(){
		...
	} // while
} // try
```

### 공로를 돌리거나 저자를 표시하는 주석
- 소스 코드 관리 시스템이 있다면 누가 언제 무엇을 추가했는지 기록할 필요가 없다.

#### 주석으로 처리한 코드
- 주석으로 처리된 코드는 다른 사람들이 지우기를 주저한다.
    - 이유가 있어 남겨 놓지않았을까 혹은 중요하니까 지우면 안된다고 생각한다.
- 소스 코드 관리 시스템이 우리를 대신해 코드를 기억해준다. 그냥 삭제하라.

#### HTML 주석
- HTML 주석은 IDE에서 조차 읽기 어렵다.
- 도구(ex. Javadocs)로 주석을 뽑아 웹페이지에 올릴 작정이라면, 주석에 HTML 태그를 삽입해야하는 책임은 프로그래머가 아니라 **도구**가 져야한다.

#### 전역 정보
- 주석을 달아야 한다면 근처에 있는 코드만 기술하라.
- 코드 일부에 주석을 달면서 시스템의 전반적인 정보를 기술하지 마라.

```java
/**
 * 적합성 테스트가 동작하는 포트 : 기본값은 <b>8082</b>
 *
 * @param fitnessePort
 */
public void setFitnessePort(int fitnessePort) {
	this.fitnessPort = fitnessePort;
}
```

> 이 함수는 포트 기본값을 전혀 통제하지 못한다. 시스템 어딘가 다른 함수를 설명하고있다.


#### 너무 많은 정보
- 주석에 흥미로운 역사나 관련 없는 정보를 장황하게 늘어놓지 마라.

#### 모호한 관계
- 주석과 주석이 설명하는 코드는 둘 관계가 명백해야한다.
- **주석을 다는 목적은 코드만으로 설명이 부족해서이다.**
- 주석 자체가 다시 설명을 요구해서는 안된다.

#### 함수 헤더
- **짧고 한 가지만 수행하며 이름을 잘 붙인 함수**가 주석으로 헤더를 추가한 함수보다 좋다.

#### 비공개 코드에서 Javadocs
- 공개 API에서는 Javadocs가 유용하지만, 비공개 코드라면 유용하지 않을 뿐 아니라 Javadocs가 요구하는 형식으로 인해 코드만 보기싫고 산만해진다.
