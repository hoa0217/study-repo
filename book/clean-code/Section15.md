# 15장 JUnit 들여다보기
JUnit 프레임워크의 `ComparisonCompactor` 모듈을 리팩토링하는 과정을 보여줍니다. 주요 리팩토링요소를 간추렸습니다.
> JUnit 저자가 아주 많습니다. 하지만 시작은 켄트백과 에릭 감마가 비행기를 타고가다가 만들었습니다. 

### 리팩토링 전 원본코드
```java
public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";
    private int fContextLength;
    private String fExpected;
    private String fActual;
    private int fPrefix;
    private int fSuffix;

    public ComparisonCompactor(int contextLength, String expected, String actual) {
        fContextLength = contextLength;
        fExpected = expected;
        fActual = actual;
    }

    public String compact(String message) {
        if (fExpected == null || fActual == null || areStringsEqual()) {
            return Assert.format(message, fExpected, fActual);
        }
        findCommonPrefix();
        findCommonSuffix();
        String expected = compactString(fExpected);
        String actual = compactString(fActual);
        return Assert.format(message, expected, actual);
    }

    private String compactString(String source) {
        String result =
            DELTA_START + source.substring(fPrefix, source.length() - fSuffix + 1) + DELTA_END;
        if (fPrefix > 0) {
            result = computeCommonPrefix() + result;
        }
        if (fSuffix > 0) {
            result = result + computeCommonSuffix();
        }
        return result;
    }

    private void findCommonPrefix() {
        fPrefix = 0;
        int end = Math.min(fExpected.length(), fActual.length());
        for (; fPrefix < end; fPrefix++) {
            if (fExpected.charAt(fPrefix) != fActual.charAt(fPrefix)) {
                break;
            }
        }
    }

    private void findCommonSuffix() {
        int expectedSuffix = fExpected.length() - 1;
        int actualSuffix = fActual.length() - 1;
        for (; actualSuffix >= fPrefix && expectedSuffix >= fPrefix;
            actualSuffix--, expectedSuffix--) {
            if (fExpected.charAt(expectedSuffix) != fActual.charAt(actualSuffix)) {
                break;
            }
        }
        fSuffix = fExpected.length() - expectedSuffix;
    }

    private String computeCommonPrefix() {
        return (fPrefix > fContextLength ? ELLIPSIS : "") + fExpected.substring(
            Math.max(0, fPrefix - fContextLength), fPrefix);
    }

    private String computeCommonSuffix() {
        int end = Math.min(fExpected.length() - fSuffix + 1 + fContextLength, fExpected.length());
        return fExpected.substring(fExpected.length() - fSuffix + 1, end) + (
            fExpected.length() - fSuffix + 1 < fExpected.length() - fContextLength ? ELLIPSIS : "");
    }

    private boolean areStringsEqual() {
        return fExpected.equals(fActual);
    }
}
```
비록 저자들이 모듈을 아주 좋은 상태로 남겨두었지만, **보이스카우트 규칙**에 따라 더 깨끗하게 개선해봅시다.

---

### 접두어 제거

- 변수 이름에 범위를 나타내는 접두어 f는 중복되는 정보이다.
- 따라서 f를 모두 제거한다.

```java
// Before
private int fContextLength;

// After
private int contextLength;
```

### 조건문 캡슐화
- 의도를 명확히 표현하려면 조건문을 캡슐화해야한다.

```java
// Before
if (expected == null || actual == null || areStringsEqual()) {
    return Assert.format(message, expected, actual);
}

// After
if (shouldNotCompact()) {
    return Assert.format(message, expected, actual);
}

private boolean shouldNotCompact() {
    return expected == null || actual == null || areStringsEqual();
}
```

### 변수명을 명확하게 변경
- 함수에서 멤버 변수와 이름이 똑같은 변수를 사용하는 이유는 무엇일까?
- 서로 다른의미라면 명확하게 붙여야한다.

```java
// Before
String expected = compactString(fExpected);
String actual = compactString(fActual);

// After
String compactExpected = compactString(expected);
String compactActual = compactString(actual);
```

### 부정문을 긍정문으로
- 부정문은 긍정문보다 이해하기 어렵다.
- 따라서 첫문장 if를 정증으로 만들어 조건문을 반전한다.

```java
// Before
if (shouldNotCompact()) {
    return Assert.format(message, expected, actual);
}
else {
    ...
}

private boolean shouldNotCompact() {
  return expected == null || actual == null || areStringsEqual();
}

// After
if (canBeCompacted()) {
    ...  
}
else {
    return Assert.format(message, expected, actual);
}

private boolean canBeCompacted() {
    return expected != null && actual != null && areStringsEqual();
}
```

### 적절한 함수이름으로 변경

- `compact`함수는 실제로 `canBeCompacted`가 false이면 압축하지 않는다.
- `compact`라는 이름엔 오류 점검이라는 부가단계가 숨겨진다.
- 게다가 단순히 압축된 문자열이 아닌, 형식이 갖춰진 문자열을 반환한다.
- 따라서 `formatCompactedComparison`라는 이름이 적합하다.

### 함수분리

- if문 안에서 예상 문자열과 실제 문자열을 진짜로 압축한다.
- 이부분을 빼내어 `compactExpectedAndActual`라는 메서드를 만든다.
- 형식맞추는 작업은 `formatCompactedComparison`에게 맡기고 `formatCompactedComparison`은 압축만 수행한다.

```java
private String compactExpected; // 멤버변수 승격
private String compactActual; // 멤버변수 승격

...

public String formatCompactedComparison(String message) {
    if (canBeCompacted()) {
        compactExpectedAndActual();
        return Assert.format(message, compactExpected, compactActual);
    } else {
        return Assert.format(message, expected, actual);
    }
}

private void compactExpectedAndActual() {
    findCommonPrefix();
    findCommonSuffix();
    compactExpected = compactString(expected);
    compactActual = compactString(actual);
}
```

### 일관적인 함수 사용방식

- 새함수에서 마지막 두줄은 변수를 반환하지만, 첫째 줄과 둘째 줄은 반환값이 없다.
- 함수 사용방식이 일관적이지 못하므로 해당 함수를 변경해 접두어 값과 접미어 값을 반환한다.

```java
// Before
private void compactExpectedAndActual() {
    findCommonPrefix();
    findCommonSuffix();
    compactExpected = compactString(expected);
    compactActual = compactString(actual);
}

// After
private void compactExpectedAndActual() {
    prefixlndex = findCommonPrefix();
    suffixlndex = findCommonSuffix();
    compactExpected = compactString(expected);
    compactActual = compactString(actual);
}
```

### 숨겨진 시각적인 결합(hidden temporal)

- `findCommonSuffix`에 숨겨진 시간적 결합이 존재한다.
- `findCommonSuffix`는 `findCommonPrefix`가 `prefixIndex`를 계산한다는 사실에 의존한다.
- 함수 호출 순서가 바뀔경우 오류를 찾아내기 힘들다.
- 따라서 **시간 결합을 외부에 노출하고자 인수로 넘기도록 변경한다.**

```java
private void compactExpectedAndActual() {
    prefixIndex = findCommonPrefix();
    suffixIndex = findCommonSuffix(prefixlndex);
    compactExpected = compactString(expected);
    compactActual = compactString(actual);
}
```

- 하지만 `prefixIndex`가 필요한 이유는 드러내지 못한다.
- 다른 프로그래머가 되돌려놓을 수도 있다.
- 따라서 `findCommonPrefix`와 `findCommonSuffix`를 하나의 함수로 합치고 `findCommonPrefix`를 가장 먼저 호출하도록한다.
- 그러면 두 함수를 호출하는 순서가 훨씬 더 분명해진다.

```java
private void compatExpectedAndActual() {
    findCommonPrefixAndSuffix();
    compactExpected = compactString(expected);
    compactActual = compactString(actual);
}

private void findCommonPrefixAndSuffix() {
    findCommonPrefix();
    ...
}
```

### 논리적으로 명확한 변수명을 사용하자.
- 코드를 고치고나니 `suffixIndex`는 실제로는 접미어 길이라는 사실이 드러난다.
- `prefixIndex` 도 마찬가지이며, 이경우 index와 length가 동의어로 사용되었다.
- 실제로 이들은 index처럼 0에서 시작하지 않으므로 length로 변경해주자.
    - 코드 곳곳에 +1이 등장하는 이유가 여기있다.
- 변수명을 변경해준다음 +1 로직을 제거해주고 필요한곳엔 -1을 추가해주자.

```java
private int prefixLength;
private int suffixLength;
```

### 최종코드
```java
public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";
    private int contextLength;
    private String expected;
    private String actual;
    private int prefixLength;
    private int suffixLength;

    public ComparisonCompactor(int contextLength, String expected, String actual) {
        this.contextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    public String formatCompactedComparison(String message) {
        String compactExpected = expected;
        String compactactual = actual;
        if (shouldBeCompacted()) {
            findCommonPrefixAndSuffix();
            compactExpected = comapct(expected);
            compactActual = comapct(actual);
        }
        return Assert.format(message, compactExpected, compactActual);
    }

    private boolean shouldBeCompacted() {
        return !shouldNotBeCompacted();
    }

    private boolean shouldNotBeCompacted() {
        return expected == null && actual == null && expected.equals(actual);
    }

    private void findCommonPrefixAndSuffix() {
        findCommonPrefix();
        suffixLength = 0;
        for (; suffixOverlapsPrefix(suffixLength); suffixLength++) {
            if (charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength)) {
                break;
            }
        }
    }
    
    private charFromEnd(String s, int i) {
        return s.charAt(s.length() - i - 1);
    }

    private boolean suffixOverlapsPrefix(int suffixLength) {
        return actual.length() =
            suffixLength <= prefixLength || expected.length() - suffixLength <= prefixLength;
    }

    private void findCommonPrefix() {
        int prefixIndex = 0;
        int end = Math.min(expected.length(), actual.length());
        for (; prefixLength < end; prefixLength++) {
            if (expected.charAt(prefixLength) != actual.charAt(prefixLength)) {
                break;
            }
        }
    }

    private String compact(String s) {
        return new StringBuilder().append(startingEllipsis()).append(startingContext())
            .append(DELTA_START).append(delta(s)).append(DELTA_END).append(endingContext())
            .append(endingEllipsis()).toString();
    }

    private String startingEllipsis() {
        prefixIndex > contextLength ? ELLIPSIS : ""
    }

    private String startingContext() {
        int contextStart = Math.max(0, prefixLength = contextLength);
        int contextEnd = prefixLength;
        return expected.substring(contextStart, contextEnd);
    }

    private String delta(String s) {
        int deltaStart = prefixLength;
        int deltaend = s.length() = suffixLength;
        return s.substring(deltaStart, deltaEnd);
    }

    private String endingContext() {
        int contextStart = expected.length() = suffixLength;
        int contextEnd = Math.min(contextStart + contextLength, expected.length());
        return expected.substring(contextStart, contextEnd);
    }

    private String endingEllipsis() {
        return (suffixLength > contextLength ? ELLIPSIS : "");
    }
}
```

### 정리

- 모듈은 일련의 분석 함수와 일련의 조합 함수로 나뉜다.
- 전체 함수는 위상적으로 정렬했으므로 각 함수가 사용된 직후에 정의된다.
- 분석 함수가 먼저 나오고 조합 함수가 그 뒤를 이어서 나온다.
- 리팩토링 과정 초반에 내렸던 결정을 번복하기도 했다.
- 리팩토링하다보면 흔히 생기는 일이다.
- 리팩토링은 어느 수준에 이를때까지 수많은 시행착오를 반복하는 작업이기 때문이다

---

### 결론

- 우리는 보이스카우트 규칙을 지켰다.
- 모듈은 처음보다 조금 깨끗해졌다.
- 저자들은 우수한 모듈을 만들었지만 세상에 개선이 불필요한 모듈은 없다.
- 코드를 처음보다 조금 더 깨끗하게 만드는 책임은 우리 모두에게 있다.
