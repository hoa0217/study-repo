# [테스트 주도 개발 - 캔트 벡](http://www.yes24.com/Product/Goods/12246033)

### 테스트 주도 개발 법칙
- 오직 자동화된 테스트가 실패할 경우에만 새로운 코드를 작성한다.
- 중복을 제거한다.

이 규칙에 의해 프로그래밍 순서가 정해진다.
1. 빨강 - 실패하는 작은 테스트를 작성한다. 처음엔 컴파일 조차 되지 않을 수 있다.
2. 초록 - 빨리 테스트가 통과하게끔 만든다. 이를 위해 어떤 **죄악**을 저질러도 좋다.
3. 리팩토링 - 일단 테스트를 통과하게만 하는 와중에 생겨난 모든 중복을 제거한다.
> **죄악** : 기존 코드 복사해서 붙이기(copy and paste), 함수가 무조건 특정 상수 반환하기 등

## 목차
### [Section1-화폐 예제 part1](Section1-1.md)
### [Section1-화폐 예제 part2](Section1-2.md)