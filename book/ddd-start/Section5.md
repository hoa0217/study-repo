# Chapter5 스프링 데이터 JPA를 이용한 조회 기능
## 5.1 시작에 앞서
#### CQRS
- 명령(Command)모델과 조회(Query)모델을 분리하는 패턴
- 명령모델: 상태를 변경하는 기능을 구현할 때 사용 ex) 회원가입, 암호변경, 주문취소
  - 엔티티, 애그리거트, 리포지터리 ➡️도메인 모델은 명령모델에 주로 사용된다.
- 조회모델: 데이터를 조회하는 기능을 구현할 때 사용 ex) 주문목록, 주문상세
  - 정렬, 페이징, 검색 조건 지정 등은 조회모델에 주로 사용된다.

> 이 장에서는 리포지터리(도메인 모델에 속한)와 DAO(데이터 접근을 의미하는)이름을 혼용해서 사용한다.

## 5.2 검색을 위한 스펙
검색 조건을 다양하게 조합해야할 때 사용할 수 있는 것이 스펙(Specification)이다.
- 스펙은 애그리거트가 특정 조건을 충족하는지 검사할 때 사용하는 인터페이스 이다.

<img width="500" alt="스크린샷 2024-05-19 오전 3 15 20" src="https://github.com/hoa0217/study-repo/assets/48192141/122f2bd1-5c55-4118-a186-3fc7f5821427">

- 스펙을 리포지터리에 사용하면 agg는 애그리거트 루트가 되고, 스펙을 DAO에 사용하면 agg는 검색 결과로 리턴할 데이터 객체가 된다.
- isSatisfiedBy메서드는 검사 대상 객체가 조건을 충족하면 true를 리턴하고, 그렇지 않으면 false를 리턴한다.
- 만약 Order 애그리거트 객체가 특정 고객의 주문인지 확인하는 스펙은 아래와 같이 구현할 수 있다.

<img width="500" alt="스크린샷 2024-05-19 오전 3 23 24" src="https://github.com/hoa0217/study-repo/assets/48192141/ebe3ad3f-922f-404a-84ff-28fbe669f3ff">

- 리포지터리나 DAO는 검색 대상을 걸러내는 용도로 스펙을 사용한다.
- 만약 리포지터리가 메모리에 모든 애그리거트를 보관하고 있다면 아래와 같이 스펙을 사용할 수 있다.

<img width="500" alt="스크린샷 2024-05-19 오전 3 24 38" src="https://github.com/hoa0217/study-repo/assets/48192141/c3004fdb-cfa4-4d78-9470-ac43ebc5048f">

- 특정 조건을 충족하는 애그리거트를 찾고 싶으면, 원하는 스펙을 생성해 리포지터리에 전달해주면 된다.

하지만 실제로 이렇게 구현하지않는다.
- 모든 애그리거트 객체를 메모리에 보관하기도 어렵고, 보관하더라도 조회 성능에 심각한 문제를 발생시킨다.
- 실제 스펙은 사용하는 기술에 맞춰 구현하게된다.
- 이 장에서는 스프링 데이터 JPA를 이용한다.

## 5.3 스프링 데이터 JPA를 이용한 스펙 구현
#### Specification(스펙 인터페이스)
<img width="500" alt="스크린샷 2024-05-19 오전 3 28 03" src="https://github.com/hoa0217/study-repo/assets/48192141/90d28131-b7c9-4e27-8fb5-727b2f98332e">

- 제네릭 타입 파라미터 T는 JPA 엔티티 타입을 의미한다.
- toPredicate()메서드는 JPA Criteria API에서 조건을 표현하는 Predicate를 생성한다.

아래에 해당하는 스펙을 다음과 같이 구현할 수 있다. 

- 엔티티 타입이 OrderSummary다.
- ordererId 프로퍼티 값이 지정한 값과 동일하다.

<img width="500" alt="스크린샷 2024-05-19 오전 3 30 16" src="https://github.com/hoa0217/study-repo/assets/48192141/533cddee-cd06-4d4c-9cec-a6d34d50e5bd">

- ordererId 프로퍼티 값이 생성자로 전달받은 ordererId와 비교하는 Predicate를 생성한다.

스펙 구현 클래스를 개별적으로 만들지 않고 별도 클래스에 스펙 생성 기능을 모아도 된다.

<img width="500" alt="스크린샷 2024-05-19 오전 3 35 05" src="https://github.com/hoa0217/study-repo/assets/48192141/0f0bd6e0-da73-47a9-b92e-4cf1dfd8b5b5">

> 스펙 인터페이스는 함수형 인터페이스이므로, 람다식을 이용해서 객체를 생성할 수 있다.

## 5.4 리포지터리/DAO에서 스펙 사용하기
스펙을 충족하는 엔티티를 검색하고 싶다면, findAll()메서드에 스펙 인터페이스를 파라미터로 사용하면 된다.

<img width="500" alt="스크린샷 2024-05-19 오전 3 37 19" src="https://github.com/hoa0217/study-repo/assets/48192141/589e901b-afff-4af0-a11b-fa52a56ca6bb">

<img width="500" alt="스크린샷 2024-05-19 오전 3 37 45" src="https://github.com/hoa0217/study-repo/assets/48192141/96c5ad17-4c7e-4355-a3ab-8f054c1884bd">

## 5.5 스펙 조합
스프링 데이터 JPA가 제공하는 스펙 인터페이스는 스펙을 조합할 수 있는 두 메서드(and, or)를 제공하고 있다.

<img width="500" alt="스크린샷 2024-05-19 오전 3 40 31" src="https://github.com/hoa0217/study-repo/assets/48192141/6fee25d9-8910-4438-97c3-51e8de8e19a0">

- and(): 두 스펙을 모두 충족하는 조건을 표현하는 스펙을 생성한다.
- or(): 두 스펙 중 하나 이상 충족하는 조건을 표현하는 스펙을 생성한다.

```java
Specification<OrderSummary> spec1 = OrderSummarySpecs.ordererId("user1");
Specification<OrderSummary> spec2 = OrderSummarySpecs.orderDateBetween(
        LocalDateTime.of(2022,1,1,0,0,0),
        LocalDateTime.of(2022,1,2,0,0,0)
        );
Specification<OrderSummary> spec3 = spec1.and(spec2);
Specification<OrderSummary> spec4 = OrderSummarySpecs.ordererId("user1")
        .and(OrderSummarySpecs.orderDateBetween(from, to)); // 한번에 선언 가능
```

- not(): 정적 메서드로 조건을 반대로 적용할 때 사용한다.

```java
Specification<OrderSummary> spec = Specification.not(OrderSummarySpecs.ordererId("user1"));
```

- where(): 정적 메서드로 null을 전달하면 아무 조건도 생성하지 않는 스펙 객체를 리턴하고, null이 아니면 인자로 받은 스펙 객체를 그대로 리턴한다.
<img width="500" alt="스크린샷 2024-05-19 오전 3 48 37" src="https://github.com/hoa0217/study-repo/assets/48192141/f00923ff-a2ce-471f-b49c-b009510d80ce">
<img width="500" alt="스크린샷 2024-05-19 오전 3 49 14" src="https://github.com/hoa0217/study-repo/assets/48192141/816a97c1-f2e9-4685-a780-53bf7c258bee">

## 5.6 정렬 지정하기
스프링 데이터 JPA는 두가지 방법을 사용해서 정렬을 지정할 수 있다.
- 메서드 이름에 OrderBy를 사용해서 정렬 기준 기정
- Sort를 인자로 전달

<img width="500" alt="스크린샷 2024-05-19 오전 3 55 26" src="https://github.com/hoa0217/study-repo/assets/48192141/b1a5e449-94cc-41b4-94a4-dd8d846d8bf1">

위 메서드는 다음 조회 쿼리를 생성한다.
- ordererId 프로퍼티 값을 기준으로 검색 조건 지정
- number 프로퍼티 값 역순으로 정렬

두개 이상의 프로퍼티에 대한 정렬 순서도 지정할 수 있다.
```java
findByOrdererIdOrderByOrderDateDeescNumberAsc
```
- 하지만, 정렬 기준이 두 개 이상이면 메서드 이름이 길어지는 단점이 있다.
- 또한 메서드 이름으로 정렬 순서가 정해지기 때문에 상황에 따라 정렬 순서를 변경할 수 도 없다.
- 이럴 때 Sort 타입을 사용하면 된다.

<img width="500" alt="스크린샷 2024-05-19 오전 3 58 59" src="https://github.com/hoa0217/study-repo/assets/48192141/f3be3772-04f9-4b96-b8e6-dbd7338883f7">

- find 메서드에 마지막 파라미터로 Sort를 받으면, 스프링 데이터 JPA는 Sort를 사용하여 알맞게 정렬 쿼리를 생성한다.
- Sort의 사용법은 아래와 같다.
```java
Sort sort = Sort.by("number").ascending();
List<OrderSummary> results = orderSummaryDao.findByOrdererId("user1", sort);

Sort sort1 = Sort.by("number").ascending();
Sort sort2 = Sort.by("orderDate").descending();
Sort sort3 = sort1.and(sort2);

Sort sort4 = Sort.by("number").ascending().and(Sort.by("orderDate").descending());
```

## 5.7 페이징 처리하기
스프링 데이터 JPA는 페이징 처리를 위해 Pageable 타입을 이용한다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 03 30" src="https://github.com/hoa0217/study-repo/assets/48192141/34183943-bac7-48d4-a6ff-371e2aa079e7">

Pageable 타입 객체는 PageRequest 클래스를 이용하여 생성한다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 07 08" src="https://github.com/hoa0217/study-repo/assets/48192141/66be4962-ccf4-4bf2-a357-1154e681216d">

- PageRequest.of(): 첫번쩨 인자는 페이지번호, 두번째 인자는 한페이지의 개수를 의미한다.
- 즉, 페이지 번호는 0번부터 시작하므로 위 코드는 11번째~20번째까지 데이터를 조회한다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 14 05" src="https://github.com/hoa0217/study-repo/assets/48192141/71118aab-f76d-472e-b703-f52b7ee6df19">

- PageRequest와 Sort를 사용하면 정렬 순서를 지정할 수 있다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 32 54" src="https://github.com/hoa0217/study-repo/assets/48192141/9804d925-fc95-44b6-9226-1e6a36f7b63a">

- Page 타입을 사용하면, 데이터 목록뿐만아니라 조건에 해당하는 전체 개수도 구할 수 있다.
- 반환 타입이 Page일 경우, 스프링 데이터 JPA는 목록 조회 쿼리와 함께 Count쿼리도 실행하여 조건에 해다하는 데이터 개수를 구한다.
- 아래는 Page가 제공하는 메서드의 일부를 보여준다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 36 46" src="https://github.com/hoa0217/study-repo/assets/48192141/5f81b50d-c8a4-4cc4-9498-a5b10fe3f173">

> 만약 반환타입이 List면 Count 쿼리를 실행하지 않는다. 따라서 페이징 처리와 관련된 정보가 필요없다면 List를 반환하여 Count쿼리를 실행하지 않도록 한다.   
> 반면 스펙을 사용하는 findAll 메서드에 Pageable 타입을 사용하면 리턴타입이 Page가 아니어도 Count쿼리를 실행한다.
```java
List<Member> findAll(Specification<Member> spec, Pageable pageable);
```

- 처음부터 N개의 데이터가 필요하면 Pageable을 사용하지 않고 findFirstN 형식의 메서드를 사용할 수 도 있다.
```java
List<Member> findFirst3ByNameLikeOrderByName(String name); // name 기준 like 검색하여 오름차순으로 정렬한 후 처음 3개를 조회한다.
```

- First 대신 Top을 사용해도된다. First나 Top뒤에 숫자가 없으면 한 개 결과만 리턴한다.
```java
Member findFirstByBlockedOrderById(boolean blocked);
```

## 5.8 스펙 조합을 위한 스펙 빌더 클래스
if와 스펙을 조합하는 코드가 섞여 있으면 실수하기 좋고 복잡한 구조를 갖는다. 따라서 아래와 같이 스펙 빌더를 만들어서 사용하자.

<img width="500" alt="스크린샷 2024-05-19 오전 4 46 36" src="https://github.com/hoa0217/study-repo/assets/48192141/07af2aed-172f-4851-93ca-0523f0aa1f60">

and(), ifTrue(), ifHasText() 외에도 필요한 메서드를 추가해서 사용하면 된다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 48 40" src="https://github.com/hoa0217/study-repo/assets/48192141/18d044d9-b724-44a7-8a91-e8c4cffe6a83">

<img width="500" alt="스크린샷 2024-05-19 오전 4 48 56" src="https://github.com/hoa0217/study-repo/assets/48192141/4c2ddd08-53b1-4fd2-9b9c-0c43168d4d8a">

## 5.9 동적 인스턴스 생성

JPA는 쿼리 결과에서 임의의 객체를 동적으로 생성할 수 있는 기능을 제공한다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 50 42" src="https://github.com/hoa0217/study-repo/assets/48192141/876fcd77-86c9-4228-8877-63cc9204ce96">

- select 절을 보면 new 키워드가 있다. new 키워드 뒤 생성할 인스턴스의 완전한 클래스 이름을 지정하고 괄호 안 생성자에 인자로 전달할 값을 지정한다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 54 12" src="https://github.com/hoa0217/study-repo/assets/48192141/93bb9d87-563c-4ee5-853f-129d1e337150">

- 조회 전용 모델을 만드는 이유는 표현 영역을 통해 사용자에게 데이터를 보여주기 위함이다.
- 많은 웹 프레임워크는 새로 추가한 밸류 타입을 알맞은 형식으로 출력하지 못하므로 위 코드처럼 값을 기본 타입으로 변환하면 편리하다.

동적 인스턴스의 장점은 JPQL을 그대로 사용하므로 객체 기준으로 쿼리를 작성하면서 동시에 지연/즉시 로딩과 같은 고민 없이 원하는 모습으로 데이터를 조회할 수 있다는 점이다.

## 5.10 하이버네이트 @Subselect 사용

하이버네이트는 JPA 확장 기능으로 @Subselect를 제공한다. Subselect는 쿼리 결과를 @Entity로 매핑할 수 있는 유용한 기등으로 아래는 사용예를 보여준다.

<img width="500" alt="스크린샷 2024-05-19 오전 4 57 16" src="https://github.com/hoa0217/study-repo/assets/48192141/95af13e6-a46d-4dd3-8df9-6602ab436836">

<img width="500" alt="스크린샷 2024-05-19 오전 4 57 46" src="https://github.com/hoa0217/study-repo/assets/48192141/2f372b06-b6cc-496c-850d-762c7acfe45b">

@Immutable, @Subselect, @Synchronize는 하이버네이트 전용 애너테이션이며 이 태그를 사용하면 @Entity로 매핑할 수 있다.

@Subselect: 조회쿼리를 값으로 갖는다. 해당 select 쿼리의 결과를 매핑할 테이블처럼 사용한다. 
- DBMS가 여러 테이블을 조인해서 조회한 결과를 한 테이블처럼 보여주기 위한 용도로 뷰를 사용하는 것 처럼 @Subslect를 사용하면 쿼리 실행 결과를 매핑할 테이블처럼 사용한다.
- 뷰를 수정할 수 없듯이 @Subselect로 조회한 @Entity 역시 수정할 수 없다.
- 만약 매핑 필드를 수정하면 하이버네이트는 변경 내역을 반영하는 update 쿼리를 실행한다. 하지만 매핑 테이블이 없으므로 에러가 발생한다.
- 위 문제를 방지하기 위해 @Immutable을 사용한다.

@Immutable: 하이버네이트는 해당 엔티티의 매핑 필드/프로퍼티가 변경되도 DB에 반영하지 않고 무시한다.

<img width="500" alt="스크린샷 2024-05-19 오전 5 10 59" src="https://github.com/hoa0217/study-repo/assets/48192141/f6a84b54-6c8c-451b-83d5-7aeb7ea55c83">

- 위 코드는 Order의 상태를 변경한뒤 OrderSummary를 조회하고 있다.
- 특별한 이유가 없으면 하이버네이트는 트랜잭션을 커밋하는 시점에 변경사항을 DB에 반영하므로, 위 코드는 최신값이 아닌 OrderSummary를 조회한다.
- 위 문제를 해소하기 위해 @Synchronize를 사용한다.

@Synchronize: 해당 엔티티와 관련된 테이블 목록을 명시한다. 이는 엔티티를 로딩하기 전, 지정한 테이블과 관련된 변경이 발생하면 flush를 먼저한다.
- 위 코드는 @Synchronize로 purchase_order 테이블을 지정하고 있으므로, 엔티티 로딩 전 해당 테이블 변경 내역을 먼저 플러시한다.

@Subselect를 사용해도 @Entity와 같기 때문에, EntityManager#find(), JPQL, Criteria를 사용해서 조회할 수 있는것이 @Subselect의 장점이다.

<img width="500" alt="스크린샷 2024-05-19 오전 5 20 56" src="https://github.com/hoa0217/study-repo/assets/48192141/2cca5365-743c-44a6-91b2-b2dd165cdf0e">

또한 이는 이름처럼 지정한 쿼리를 from 절의 서브쿼리로 사용한다. 먼역 서브쿼리를 사용하고 싶지않다면, 네이티브 SQL쿼리를 사용하거나 마이바티스와 같은 별도 매퍼를 사용해서 조회 기능을 구현해야한다.

