# 1장 깨끗한 코드

[[구름 Commit 참석 후기] ‘클린 코드’가 코드에 대한 책이 아닌 사실에 대해](https://velog.io/@gjwjdghk123/구름-Commit-참석-후기-클린-코드가-코드에-대한-책이-아닌-사실에-대해-m21z5z6c)

> 책을 읽기 전, 위 세션을 듣고 후기를 작성하였습니다.
간략 요약하자면 클린코드만이 정답은 아니라는 것이며 기법의 권고 정도로 인지하고 읽는 것이 좋다는 것을 깨달았습니다.
>

## 도입

### 코드가 존재하리라

- 코드의 종말이 닥쳤다느니, 코드를 자동으로 생성한다느니 해도 앞으로 코드가 사라질 가망은 절대 없다.
- **코드의 도움없이 요구사항을 상세히(정밀히) 표현할 수 없기 때문.**

### 나쁜 코드

- 켄트백의 구현패턴 : `좋은 코드가 중요하다는 다소 미약한 전제에 기반한다`
    - 이 책은 이에 동의하지 않는다.
- 기한을 지키기위해 마구잡이로 작성한 나쁜 코드는 초반엔 빠를지언정, 나중에 개발 속도를 크게 떨어뜨린다.
    - 얽히고 설킨 코드를 해석하고 더한다 → 간단한 변경 불가능

### 태도

- 일정이 촉박해서 제대로 할 시간이없다. 멍청한 관리자와 조급한 고객 탓이다. 과연?
- 개발자는 프로젝트 계획 과정에 깊숙히 관여하므로 프로젝트 실패, 특히 나쁜코드로 초래하는 실패에 책임이 크다.
- 나쁜 코드의 위험을 이해하지 못하는 관리자를 그대로 따르는건 전문가 답지 못하다.

### 원초적 난제

- 나쁜 코드를 양산하면 기한을 맞추지 못한다. → 오히려 엉망친창인 상태로 속도가 늦어짐.
- 언제나 코드를 깨끗하게 유지하는 습관이 방법이다.

## 깨끗한 코드란?

노련한 프로그래머들의 다양한 정의가 존재한다.

### **비야네 스트롭스트룹(Bjarne Stroustrup)**

`나는 우아하고 효율적인 코드를 좋아한다.`

- 우아한 코드 : 외양이나 태도가 기품 있고 단아하며 보기에 즐거운 코드
- 효율적인 코드 : 단순 속도만을 뜻하지 않으며, CPU의 자원을 낭비하지 않는 코드

`성능을 최적으로 유지해야 사람들이 원칙없는 최적화로 코드를 망치려는 유혹에 빠지지 않는다.`

- 나쁜코드는 나쁜코드를 유혹한다. 고치면서 오히려 더 나쁜 코드를 만든다.

`오류는 명백한 전략에 의거해 철저히 처리한다.`

- 철저하게 오류 처리된 코드 : 메모리 누수, race condition, 일관성 없는 명명법 등 세세한 사항까지 꼼꼼하게 처리하는 코드

`깨끗한 코드는 한가지를 제대로 한다.`

- 깨끗한 코드 : 너무 많은 일을 하려 애쓰지 말고 한가지 일에 집중하는 코드
- 각 함수와 클래스와 모듈은 주변 상황에 현혹되거나 오염되지 않는 채 한길만 걷자.

### 그래디 부치(Grady Booch)

`깨끗한 코드는 잘 쓴 문장처럼 읽힌다.`

- 가독성 : 해결할 문제와 해법을 명백히 제시하고 풀어야한다.

`깨끗한 코드는 결코 설계자의 의도를 숨기지 않는다. 오히려 명쾌한 추상화와 단순한 제어문으로 가득하다.`

- 코드는 사실에 기반하며, 필요한 내용만 담아야한다.

### 큰(Big) 데이브 토마스(Dave Thomas)

`깨끗한 코드는 작성자가 아닌 사람도 읽기 쉽고 고치기 쉽다.`

- 실제로 읽기 쉬운코드와 고치기 쉬운 코드는 다르다.

`단위테스트케이스와 인수테스트케이스가 존재한다.`

- **테스트 케이스**가 없는 코드는 아무리 가독성이 높아도 깨끗하지 않다.

### 마이클 패더스(Michael Feathers)

`깨끗한 코드는 언제나 누군가 주의 깊게 짰다는 느낌을 준다.`

- 깨끗한 코드 : 시간을 들여 깔끔하고 단정하게 정리한 코드

### 론 제프리스(Ron Jeffries)

```
**켄트 벡의 코드 규칙**
- 모든 테스트를 통과한다.
- **중복**이 없다.
- 시스템 내 모든 설계 아이디어를 표현한다.
- 클래스, 메서드, 함수 등을 최대한 줄인다.
```

> 결론 : 중복 줄이기, 한기능만 수행하기, 제대로 표현(의미있는 이름)하기, 작게 추상화하라.
>

### 워드 커닝햄(Ward Cunningham)

`코드를 읽으면서 짐작했던 기능을 각 루틴이 그대로 수행한다면 깨끗한 코드라 불러도 되겠다.`

- 깨끗한 코드는 읽으면서 놀랄일이 없어야한다.
- 짐작한 대로 돌아가는 코드가 깨끗한 코드이다.

## 결론

### 우리는 저자다

- 우리는 새코드를 짜면서 끊임없이 기존 코드를 읽는다.
- 따라서 읽기 쉬운 코드는 매우 중요하며, 읽기 쉽게 만들면 짜기도 쉬워진다.

### 보이스카우트 규칙

`캠핑장은 처음 왔을 때 보다 더 깨끗하게 해놓고 떠나라`

- 체크아웃할 때보다 좀 더 깨끗한 코드를 체크인하자.
- 변수이름하나를 개선하고, 조금 긴 함수 하나를 분할하고, 약간의 중복을 제거하고, 복잡한 if문을 정리하자. 이정도면 충분.

> 시간이 지날 수록 코드가 좋아지는 프로젝트를 만들자.

### 클린 코드

이 책은 **오브젝트 멘토 진영이 생각하는 깨끗한 코드**를 설명한다. 이 책을 읽는다고 뛰어난 프로그래머가 된다는 보장은 없으며, 뛰어난 프로그래머가 생각하는 방식과 사용하는 기술, 기교, 도구를 소개하고 권고한다.
