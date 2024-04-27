# Transaction
J가 H에게 20만원을 이체한다면 각자의 계좌는 어떻게 변경돼야 할까요?

![스크린샷 2024-04-27 오후 12 20 12](https://github.com/f-lab-edu/modoospace/assets/48192141/bbb5668d-e920-453b-af49-9ccdfbe9bc98)

- 두 SQL문 전부다 성공을 해야만 이체라는 작업은 성공한다.
- 하나는 성공 하나는 실패하게되면 이는 정상적인 이체가 아니다.

![스크린샷 2024-04-27 오후 12 21 58](https://github.com/f-lab-edu/modoospace/assets/48192141/ae8cb20a-d025-4356-bc22-1aa22d80c341)

- 이런 작업을 Database에서는 Transaction이라 부른다.

## Transaction이란
- 단일한 논리적인 작업 단위(a single logical unit of work)
- 논리적인 이유로 여러 SQL문들을 단일 작업으로 묶어서 나눠질 수 없게 만든 것이 transaction이다.
- transaction의 SQL문들 중 일부만 성공해서 DB에 반영되는 일은 일어나지 않는다.

## COMMIT과 ROLLBACK
```sql
START TRANSACTION
UPDATE account SET balance = balance - 200000 WHERE id = 'J';
UPDATE account SET balance = balance + 200000 WHERE id = 'H';
COMMIT;
```
> COMMIT: 지금까지 작업한 내용을 DB에 영구적(permanently)으로 저장하라. Transaction을 종료한다.

```sql
START TRANSACTION
UPDATE account SET balance = balance - 300000 WHERE id = 'J';
ROLLBACK ;
```
> ROLLBACK: 지금까지 작업들을 모두 취소하고 transaction 이전상태로 되돌린다. Transaction을 종료한다.

## AUTOCOMMIT
- 각각 SQL문을 자동으로 transaction 처리해주는 개념
- SQL문이 성공적으로 실행하면 자동으로 COMMIT한다.
- 실행중에 문제가 있었다면 알아서 ROLLBACK한다.
- MySQL에서는 default로 autocommit이 enabled되어있다.
- 다른 DBMS에서도 대부분 같은 기능을 제공한다.
```sql
// autocommit 기능이 활성화 되어있는지 확인
SELECT @@AUTOCOMMIT;
```
![스크린샷 2024-04-27 오후 12 35 47](https://github.com/f-lab-edu/modoospace/assets/48192141/cbdfb028-4fe9-4d36-b5a3-8a7b14d83b63)
```sql
// autocommit 기능이 비활성화
SET autocommit = 0;
```
![스크린샷 2024-04-27 오후 12 36 44](https://github.com/f-lab-edu/modoospace/assets/48192141/22b92691-bf28-482b-8de7-018c5bd1af50)
- autocommit을 off한 후 delete를 했기 때문에
- rollback을 한다면 다시 이전 상태로 돌아갈 수 있다.

### 그럼 위에 예제도 AutoCommit된거 아닌가?
```sql
START TRANSACTION
UPDATE account SET balance = balance - 200000 WHERE id = 'J';
UPDATE account SET balance = balance + 200000 WHERE id = 'H';
COMMIT;
```
- START TRANSACTION 실행과 동시에 autocommit은 off된다
- COMMIT/ROLLBACK과 함께 transaction이 종료되면 원래 autocommit상태로 돌아간다.

## 일반적인 Transaction 사용 패턴
1. transaction을 시작(begin)한다
2. 데이터를 읽거나 쓰는 등의 SQL문들을 포함해서 로직을 수행한다
3. 일련의 과정들이 문제없이 동작했다면 transaction을 commit한다
4. 중간에 문제가 발생했다면 transaction을 rollback한다

## Java에서는 어떻게 작성할까?

![스크린샷 2024-04-27 오후 12 43 01](https://github.com/f-lab-edu/modoospace/assets/48192141/a7c25d42-2a7e-4043-8b4d-9fa823cb41e0)

> Connection은 재사용되기때문에 Autocommit을 다시 켜준다.

### 하지만 Transaction과 관련된 코드를 분리하고싶다면?
![스크린샷 2024-04-27 오후 12 44 34](https://github.com/f-lab-edu/modoospace/assets/48192141/82154f4b-53d0-4948-8044-368f49b27f65)

> 스프링을 사용한다면 @Transaction만 붙이면된다.   
> Transaction과 관련된 부가적인 코드 숨기기 가능. (Spring AOP)

## ACID
### Atomicity(원자성)
- All or Nothing
- transaction은 논리적으로 쪼개질 수 없는 작업 단위이기 때문에 내부의 SQL문들이 모두 성공해야 한다.
- 중간에 SQL문이 실패하면 지금까지의 작업을 모두 취소하여 아무 일도 없었던거처럼 rollback 한다.
```sql
START TRANSACTION
UPDATE account SET balance = balance - 200000 WHERE id = 'J';
UPDATE account SET balance = balance + 200000 WHERE id = 'H';
COMMIT;
```
- commit 실행시 db에 영구적으로 저장하는 것은 dbms가 담당하는 부분이다.
- rollback 실행시 이전상태로 되돌리는 것도 dbms가 담당하는 부분이다.
- 개발자는 언제 commit하거나 rollback할지를 챙겨야한다.

### Consistency(일관성)

![스크린샷 2024-04-27 오후 12 50 21](https://github.com/f-lab-edu/modoospace/assets/48192141/b26625a5-e2a3-41f3-999d-5af3a4c0968d)

- 테이블 생성시 balance는 음수가 될 수 없다고 제약사항을 걸어둠
- 하지만 첫번째 쿼리는 해당 제약사항을 깨뜨리므로 데이터의 일관성을 깨뜨리므로 Rollback이 수행된다.

Consistency
- transaction은 DB상태를 consistent상태에서 또다른 consistent 상태로 바꿔줘야한다.
- constraints, trigger 등을 통해 DB에 정의된 rules을 transaction이 위반했다면 rollback해야한다.
- transaction이 db에 정의된 rule을 위반했는지 DBMS가 commit 전 확인하고 알려준다.
- 그 외 applicaton 관점에서 transaction이 consistent하게 동작하는지는 개발자가 챙겨야한다.

### Isolation(격리성)
![스크린샷 2024-04-27 오후 12 57 11](https://github.com/f-lab-edu/modoospace/assets/48192141/ad45b174-b40c-4232-9f9f-ab271c9185a7)
- 20만원 입금 도중, 30만원이 입금이 되었으나 이미 읽어온 값이 있어 30만원이 증발함.

Isolation
- 여러 transaction들이 동시에 실행될 때도 혼자 실행되는 것처럼 동작하게 만든다.
- DBMS는 여러 종류의 isolation level을 제공한다.
- 개발자는 isolation level중 어떤 level로 transaction을 동작시킬지 설정할 수 있다.
- concurrency control의 주된 목표가 isolation이다.

### Durablity(영속성)
![스크린샷 2024-04-27 오후 1 02 59](https://github.com/f-lab-edu/modoospace/assets/48192141/9efa4275-49b1-4c78-a184-5e5d620663c3)
- commit된 transaction은 db에 영구적으로 저장한다.
- 즉, db system에 문제(power fail or DB crash)가 생겨도 commit된 transaction은 DB에 남아있는다.
- 영구적으로 저장한다라고 할때는 일반적으로 비휘발성 메모리(HDD, SSD...)에 저장함을 의미한다.
- 기본적으로 transaction의 durability는 DBMS가 보장한다.

## 참고사항
1. transaction을 어떻게 정의해서 쓸지는 개발자가 정하는 것.
   - 구현하려는 기능과 ACID속성을 이해해야 transaction을 잘 정의할 수 있다.
2. transaction의 ACID와 관련해서 개발자가 챙겨야하는 부분들이 있다.
   - DBMS가 모든것을 알아서 해주는 것은 아니다.
