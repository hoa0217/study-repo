# 5장 형식 맞추기

### 형식을 맞추는 목적
- 코드형식은 의사소통의 일환이다.
    - 의사소통은 전문 개발자의 일차적 의무다.
- 오늘 구현한 기능이 다음 버전에서 바뀔 확률은 아주 높지만, **오늘 구현한 코드의 가독성은 앞으로 바뀔 코드의 품질에 지대한 영향을 미친다.**
- 원래 코드는 사라지더라도 개발자의 스타일과 규율은 사라지지 않는다.

### 적절한 행 길이를 유지하라
- 유명한 프로젝트(Junit, Fitnesse 등 7개)의 파일 길이(행 수)를 조사한 결과
    - 500줄을 넘지 않고 대부분 **200줄** 정도이다.
- 즉, 작은 **파일**로도 커다란 시스템을 구축할 수 있다.
- **일반적으로 큰 파일보다 작은 파일이 이해하기 쉽다.**

#### 신문 기사처럼 작성하라
- 신문기사
    - 최상단 : 기사를 몇 마디로 요약하는 **표제**. 독자는 표제로 읽을지 말지 결정한다.
    - 첫문단 : 전체 기사 내용을 요약. 세세한 사실은 숨기고 커다란 그림을 보여준다.
    - 나머지 : 쭉 읽으며 내려가면 세세한 사실은 조금씩 드러난다.
- 소스코드
    - 이름 : 독자가 **이름**만 보고도 올바른 모듈인지 판단 가능할 정도로 짓되 간단하게 짓는다.
    - 첫부분 : 고차원 개념과 알고리즘을 설명한다.
    - 나머지 : 아래로 내려갈수록 의도를 세세하게 묘사하며, 마지막엔 가장 저차원 함수와 세부내역이 나온다.

#### 개념은 빈 행으로 분리하라
- 모든 코드는 왼쪽 → 오른쪽, 위 → 아래 로 읽힌다.
- 각 행은 수식이나 절을 나타내고, 일련의 행 묶음은 완결된 생각 하나를 표현한다.
- **생각 사이는 빈 행을 넣어 분리해야 마땅하다.**

```java
// 분리된 예시
package fitness.wikitext.widgets;

import java.util.regex.*;

public class BoldWidget extends ParentWidget {
	public static final String REGEXP = "'''.+?'''";
	public static final Pattern pattern = Pattern.complie("'''(.+?)'''", Pattern.MULTILINE + Pattern.DOTALL);

	public BoldWidget(ParentWidget parent, String text) throws Exception {
		...
	}

	public String render() throws Exception {
		...
	}
}
```

```java
// 분리되지 않은 예시
package fitness.wikitext.widgets;
import java.util.regex.*;
public class BoldWidget extends ParentWidget {
	public static final String REGEXP = "'''.+?'''";
	public static final Pattern pattern = Pattern.complie("'''(.+?)'''", Pattern.MULTILINE + Pattern.DOTALL);
	public BoldWidget(ParentWidget parent, String text) throws Exception {
		...
	}
	public String render() throws Exception {
		...
	}
}
```

#### 세로 밀집도
- 줄바꿈이 개념을 분리한다면, **세로 밀집도는 연관성을 의미한다.**
- 서로 밀접한 코드 행은 세로로 가까이 놓여야 한다는 뜻이다.

```java
public class TestConfig {

	private String className;
	private List<Property> properties = new ArrayList<Property>();

	public void addProperty(Property property) {
		properties.add(property);
	}

	public void clearProperty() {
		properties.removeAll();
	}
}
```
> 인스턴스 변수끼리 모아놓는다면, 해당 클래스는 변수2개 메서드 2개인 클래스라는 사실이 **한눈에** 들어온다.


#### 수직 거리
- 서로 밀접한 개념은 세로로 가까이 둬야한다.
- 타당한 근거가 없다면 서로 밀접한 개념은 한 파일에 속해야 마땅하다.
- *이게 바로 protected 변수를 피해야 하는 이유 중 하나다.*
    - 접근제어자 `protected` : 동일한 패키지 내에 존재하거나 파생클래스에서만 접근 가능
    - 내생각 : 만약 해당 변수가 어디서 나온 변수인지 알고싶다면 **부모클래스(다른 파일)까지 뒤져야할 가능성이 있다.**
- 같은 파일에 속할 정도로 밀접한 두 개념은 **세로 거리로 연관성을 표현한다.**
    - 연관성 : 한 개념을 이해하는 데 다른 개념이 중요한 정도

#### 변수 선언
변수는 사용하는 위치에 최대한 가까이 선언한다.

```java
private static void readPreferences() {
	**InputStream is = null;**
	try {
		is = new FileInputStream(getPreferencesFile());
		...
	}
	catch (IOException e){
		...
	}
}
```

```java
public int countTestCases() {
	int count = 0;
	for (**Test each** : tests){
		...
	}
	return count;
}
```

#### 인스턴스 변수
- 클래스 맨 처음에 선언한다.
- **변수 간 세로로 거리를 두지 않는다.**
- 잘 설계한 클래스는 **클래스 메서드가 인스턴스 변수를 사용하기 때문이다.**
- 잘 알려진 위치에 인스턴스 변수를 모은다는 사실이 중요하다.
> C++은 맨 마지막, Java는 맨 앞에 선언한다.

#### 종속 함수
- 한 함수가 다른 함수를 호출한다면 두 함수는 세로로 가까이 배치한다.
- 가능하다면 **호출하는 함수를 호출되는 함수보다 먼저 배치한다.**
- 이 규칙을 일관적으로 적용한다면, 독자는 방금 호출한 함수가 잠시 후 정의될것이라 예측한다.

```java
public class PageClass {
	private Page page;

	public Response makePage(Context context, Request request) {
		String pageName = getPage(request, "FrontPage");
		loadPage(pageName, context);
		if (page == null) {
			return notFoundResponse(context);
		}
		return makePageResponse(context);
	}

	private String getPage(Request request, String defaultPageName) {
		String pageName = request.getResource();
		if (StringUtil.isBlank(pageName))
			pageName = defaultPageName;
		return pageName;
	}

	private void loadPage(String pageName, Context context) {
		...
	}

	private Response notFoundResponse(Context context) {
		...
	}

	private Response makePageResponse(Context context) {
		...
	}
}
```
- "FrontPage" 를 상수로 사용하는 방법도 있다.
- 하지만, 기대와 달리 **잘 알려진 상수가 적절하지 않은 저차원 함수에 묻힌다.**
- **상수를 알아야 마땅한 함수(`makePage`)에서 실제로 사용하는 함수(`getPage`)로 상수를 넘겨주는 방법이 더 좋다.**

> 내생각 : 상수를 고차원함수에서 넘겨줌으로써 저차원 함수인 `getPage` 를 다른곳에서 재사용할 수 있는 가능성이 생김.

### 개념적 유사성
- **친화도**가 높을수록 코드를 가까이 배치한다.
- 친화도가 높은 예시
    - 한 함수가 다른 함수를 호출해 생기는 직접적인 종속성
    - 변수와 그 변수를 사용하는 함수
    - **비슷한 동작을 수행하는 일군의 함수**

```java
// Junit 4.3.1
public class Assert {
	static public void assertTrue(String message, boolean condition){
		if (!condition)
			fail(message);
	}

	static public void assertTrue(boolean condition){
		assertTrue(null, condition);
	}

	static public void assertFalse(String message, boolean condition){
		assertTrue(message, !condition);
	}
	
	static public void assertFalse(boolean condition){
		assertFalse(null, condition);
	}
}
```

> 이들은 **명명법도 똑같고 기본 기능이 유사하고 간단하다.** (서로가 서로를 호출하는 관계는 부차적 요인)

#### 세로 순서
- 함수 호출 종속성은 아래방향으로 유지한다.
- 그러면 소스 코드 모듈이 고차원에서 저차원으로 자연스럽게 내려간다.

### 가로 형식 맞추기
- 유명한 프로젝트(Junit, Fitnesse 등 7개)의 **행 길이** 분포 조사 결과
    - 20~60자 `40%`, 10자 미만 `30%`, 80자 이후부터는 행 수 급격 감소한다.
- **프로그래머는 명백하게 짧은 행을 선호한다.**
- *엉클밥은 120자 정도로 행길이를 제한한다.*

#### 가로 공백과 밀집도
- 가로로 공백을 사용해 밀접한 개념과 느슨한 개념을 표현한다.

```java
// 할당연산자 강조를 위해 앞뒤 공백을 줌.
int lineSize = line.length();

// 함수와 인수는 밀접하기 때문에 함수이름과 괄호사이에 공백을 넣지 않음.
recordWidestLine(lineSize);

// 괄호한 인수는 공백으로 분리함. 쉼표를 강조해 인수가 별개라는 사실을 보여주기 위해.
addLine(lineSize, lineCount);
```

#### 가로 정렬
- 가로로 정렬을 하게 되면 엉뚱한 부분을 강조해 진짜 의도가 가려진다.

```java
private    boolean     a;
private    boolean     b;
private    boolean     c;
```
> 변수 타입은 무시하고 이름부터 읽게됨.

- 정렬이 필요할 정도로 목록이 길다면, **문제는 목록 길이**이지 정렬 부족이 아니다.
- **만약 선언부가 길다면 클래스를 쪼개야 한다는 의미이다.**

#### 들여쓰기
- **들여쓰기한 파일은 구조가 한눈에 들어온다.**
    - 하지 않는다면 코드를 열심히 분석해야한다.
- 때로 간단한 if, while, 함수에서 들여쓰기를 무시하고픈 유혹이 생기지만, 엉클밥은 들여쓰기를 넣는걸 선호한다.

```java
// 들여쓰기 x
public String render() throws Exception {return "";}

// 들여쓰기 o
public String render() throws Exception {
	return "";
}
```

### 팀 규칙
- 팀은 한가지 규칙을 합의해야하고 모든 팀원은 그 규칙을 따라야 한다.
- 그리고 해당 규칙으로 IDE 코드 형식기를 설정한 후 사용한다.
- 좋은 소프트웨어는 읽기 쉬운 문서로 이루어져야한다.
- 스타일은 일관적이고 매끄러워야한다.
- **한 소스 파일에 봤던 형식이 다른 소스 파일에도 쓰이리라는 신뢰감을 줘야한다.**
