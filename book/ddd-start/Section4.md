# Chapter4 리포지터리와 모델 구현 
## 4.1 JPA를 이용한 리포지터리 구현
- 애그리거트를 어떤 저장소에 저장하느냐에 따라 구현방법이 다르다.
- 도메인 모델과 리포지터리를 구현할 때 선호하는 기술을 뽑자면 JPA다.
- 데이터 보관소로 RDBMS를 사용할 때, 객체 기반의 도메인 모델과 관계형 데이터 모델 간 매핑을 처리하는 기술로 ORM만한것이 없다.

### 4.1.1 모듈 위치
리포지터리 인터페이스는 애그리거트와 같은 도메인 영역에 속하고, 리포지터리를 구현한 클래스는 인프라스트럭처 영역에 속한다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 08 08" src="https://github.com/hoa0217/study-repo/assets/48192141/07845bc4-41c3-44ec-a217-b8c23d7c3323">

팀 표준에 따라 구현 클래스를 `domain.impl`과 같은 패키지에 위치시킬 수 도 있지만, 이건은 인터페이스와 구현체를 분리하기 위한 타협안 같은 것이지 좋은 설계 원칙을 따르는 것은 아니다.
- 가능하면 리포지터리 구현 클래스를 인프라스트럭처 영역에 위치 시켜 인프라스트럭처에 대한 의존을 낮춰야한다.

### 4.1.2 리포지터리 기본 기능 구현

- ID로 애그리거트 조회하기
- 애그리거트 저장하기

기본 기능을 제공하기 위해 리포지터리는 아래와 같은 형식을 갖는다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 12 18" src="https://github.com/hoa0217/study-repo/assets/48192141/566d9992-20dc-4fe1-b011-f19a0944793c">

- 주문 애그리거트는 다양한 객체를 포함하지만, 루트 엔티티은 Order를 기준으로 인터페이스를 작성한다.

#### ID로 애그리거트 조회하기
- 조회 기능 이름에 특별한 규칙은 없지만 널리 사용되는 규칙은 `findBy프로퍼티이름` 형식을 사용한다. ex) `findById()`
- 해당 ID에 애그리거트가 존재하면 객체를 리턴하고 아니면 null을 리턴한다.
- null을 사용하고 싶지 않으면 Optional을 사용한다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 16 53" src="https://github.com/hoa0217/study-repo/assets/48192141/6d897f16-c695-4ea1-acef-ad69b2e981ed">

#### 애그리거트 저장하기
- save는 전달받은 애그리거트를 저장한다.

#### JPA EntityManager로 기능을 구현하면?
<img width="500" alt="스크린샷 2024-05-12 오후 5 17 59" src="https://github.com/hoa0217/study-repo/assets/48192141/30b0ff62-9cbf-41ae-9e90-a45b50be9340">

> 실제로 스프링 데이터 JPA가 알아서 만들어주므로 구현할 필요는 없다.

#### 애그리거트 수정하기
- 애그리거트를 수정하는 메서드는 추가할 필요가 없다.
- JPA를 사용하면 트랜잭션 범위에서 변경한 데이터를 자동으로 DB에 반영하기 때문이다.
- 즉, 트랜잭션을 커밋하기 직전 트랜잭션 범위에서 변경된 객체의 데이터를 반영하기 위해 UPDATE 쿼리를 자동으로 실행한다.

#### ID가 아닌 다른조건으로 애그리거트 조회하기
- findBy뒤에 조건 대상이 되는 프로퍼티 이름을 붙인다.
- 예를 들어 특정 ID가 주문한 Order목록을 구하는 메서드는 아래와 같이 정의할 수 있다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 31 47" src="https://github.com/hoa0217/study-repo/assets/48192141/a33f5f1e-1646-421e-bf71-b58b23fbebf4">

<img width="500" alt="스크린샷 2024-05-12 오후 5 33 18" src="https://github.com/hoa0217/study-repo/assets/48192141/c8aa4bca-072d-4ce2-a194-249df9156af4">

> Criteria나 JPQL을 사용할 수 있으며 위에 예제는 JPQL을 사용하였다.

#### 애그리거트 삭제하기

<img width="500" alt="스크린샷 2024-05-12 오후 5 34 05" src="https://github.com/hoa0217/study-repo/assets/48192141/42ed4221-2ae7-4dec-b963-d0e34f96756f">

<img width="500" alt="스크린샷 2024-05-12 오후 5 34 27" src="https://github.com/hoa0217/study-repo/assets/48192141/b4443b54-0a8f-4f9c-b46b-a5c26b16bd87">

> 삭제 요구사항이 있더라도, 데이터를 실제 삭제하는 경우는 많지 않다. 관리자 기능에서 삭제한 데이터까지 조회해야하는 경우도 있고 데이터 원복을 위해 일정 기간 동안 보관해야할 때도 있기 때문이다.   
> 이런 이유로 삭제 기능실행 시 데이터를 바로 삭제하기 보다 삭제 플래그를 사용해서 데이터 화면에 보여줄 지 여부를 결정하는 방식으로 구현한다.

## 4.2 스프링데이터 JPA를 이용한 리포지터리 구현

스프링 데이터 JPA는 지정한 규칙에 맞게 인터페이스를 정의하면 리포지터리를 구현한 객체를 알아서 만들어 스프링빈으로 등록해준다.
- 직접 구현하지 않아도 되기 때문에, 개발자는 리포지터리를 쉽게 정의할 수 있다.

스프링 데이터 JPA는 다음 규칙에 따라 작성한 인터페이스를 찾아 인터페이스를 구현한 스프링 빈 객체를 자동으로 등록하다.
- org.springframwork.data.repository.Repository<T, ID> 인터페이스 상속
- T는 엔티티 타입을 지정하고 ID는 식별자 타입을 지정

만약 Order의 식별자 타입이 OrderNo라면? 아래와 같이 작성할 수 있다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 39 03" src="https://github.com/hoa0217/study-repo/assets/48192141/862b54d2-86b5-44f8-a7f4-92228ce808bc">

스프링 데이터 JPA는 OrderRepository를 리포지터리로 인식하여 알맞게 구현한 객체를 스프링빈으로 등록한다.
- 만약 OrderRepository가 필요하면, 다음 코드처럼 주입받아 사용하면 된다.
```java
@Service
public class CancelOrderService {
    private OrderRepository orderRepository;
    
    public CancelOrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
    ...
}
```

스프링 데이터 JPA를 사용하려면 지정한 규칙에 맞게 메서드를 작성해야한다.

#### 엔티티 저장
- Order save(Order entity)
- void save(Order entity)

#### 식별자를 이용한 엔티티 조회
- Order findById(OrderNo id)
- Optional<Order> findById(OrderNo id)

#### 특정 프로퍼티를 이용한 엔티티 조회
- List<Order> findByOrderer(Orderer orderer)

#### 중첩 프로퍼티를 이용한 엔티티 조회
- List<Order> findByOrdererMemberId(MemberId memberId)

#### 엔티티삭제
- void delete(Order order)
- void deleteById(OrderNo id)

## 4.3 매핑구현
### 4.3.1 엔티티와 밸류 기본 매핑 구현
기본 규칙
- 애그리거트 루트는 엔티티이므로 @Entitiy로 매핑 설정한다.

한 테이블에 엔티티와 밸류 데이터가 같이 있다면?
- 밸류는 @Embaddable로 매핑 설정한다.
- 밸류 타입 프로퍼티는 @Embedded로 매핑 설정한다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 45 44" src="https://github.com/hoa0217/study-repo/assets/48192141/258e5d44-a98d-427f-b2c2-59f2a3c5aa9a">

- 위 예시를 보면 Order는 루트 엔티티, Orderer 밸류이다.
- 이를 JPA로 매핑을 해보면 아래와 같다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 47 20" src="https://github.com/hoa0217/study-repo/assets/48192141/b65e17e9-d9d1-47f8-9840-5c206fc19078">

<img width="500" alt="스크린샷 2024-05-12 오후 5 47 49" src="https://github.com/hoa0217/study-repo/assets/48192141/08cf6793-50ee-4f9b-afd3-c570de93aa0f">

<img width="500" alt="스크린샷 2024-05-12 오후 5 49 07" src="https://github.com/hoa0217/study-repo/assets/48192141/0feb9c82-ded5-4f94-855d-156b055b16f3">

- 여기서 Orderer의 MemberId는 Member 애그리거트를 ID로 참조한다.
- 그리고 MemberId는 다음과 같이 id프로퍼티와 매핑되는 테이블 칼럼 이름으로 member_id를 지정하고 있다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 51 22" src="https://github.com/hoa0217/study-repo/assets/48192141/2a77c9b3-f464-4a9a-9cf2-5f6d1ccbab10">

> @AttributeOverrides는 Orderer의 memberId 프로퍼티와 매핑할 칼럼을 변경했다. member_id -> orderer_id

### 4.3.2 기본 생성자
- 엔티티와 밸류의 생성자는 객체를 생성할 때 필요한 것을 전달받는다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 54 34" src="https://github.com/hoa0217/study-repo/assets/48192141/46f610b0-cdb7-44f9-a924-ccdd24038ef9">

- 여기서 Receiver가 불변 타입이면 생성 시점 필요한 값을 모두 전달받으므로 set 메서드는 제공하지 않는다.
- 즉, 기본생성자를 추가할 필요가 없다는 것을 의미한다.
- 하지만 JPA에서 @Entity와 @Embaddable로 클래스 매핑하려면 기본생성자를 제공해야한다.
  - DB에서 데이터를 읽어와 매핑된 객체를 생성할 때 기본 생성자를 사용하여 객체를 생성하기 때문이다.
- 이런 기술적 제약으로 필요없음에도 불구하고 **기본 생성자**를 추가해야한다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 56 21" src="https://github.com/hoa0217/study-repo/assets/48192141/226d657e-cb96-4380-b463-592a334e1afd">

> protected로 선언한 이유는 다른코드에서 기본생성자를 사용하지 못하도록 막기 위해서 이다.
