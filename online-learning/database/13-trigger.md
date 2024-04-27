# Trigger
## SQL에서 Trigger?
데이터베이스에서 어떤 이벤트가 발생했을 때 자동적으로 실행되는 프로시저(procedure)

데이터에 변경(insert, update, delete)이 생겼을 때, 이것이 계기가 되어 자동적으로 실행되는 프로시저(procedure)를 의미

### 예시1
![스크린샷 2024-04-27 오전 11 49 05](https://github.com/f-lab-edu/modoospace/assets/48192141/3fc72780-23b5-4d18-b1cc-e927427474b8)
- nickname이 업데이트 될때마다 users_log에 닉네임 변경 이력을 저장하는 트리거가 발생한다.
```sql
delimiter $$
CREATE TRIGGER log_user_nickname_trigger
BEFORE UPDATE 
ON users FOR EACH ROW 
BEGIN 
    insert into users_log values(OLD.id, OLD.nickname, now());
END $$
delimiter ;
```
> `OLD`: update/delete 되기전의 tuple을 가리킴.

### 예시2
![스크린샷 2024-04-27 오전 11 59 49](https://github.com/f-lab-edu/modoospace/assets/48192141/0c126509-96e4-43d4-a9ba-9dc36641765e)
- 사용자가 마트에서 상품을 구매할 때마다 지금까지 누적된 구매 비용을 구하는 트리거가 발생한다.
```sql
delimiter $$
CREATE TRIGGER sum_by_prices_trigger
AFTER INSERT
ON buy FOR EACH ROW 
BEGIN
    DECLARE total INT;
    DECLARE user_id INT DEFAULT NEW.user_id;
    
    select sum(price) into total from buy where user_id = user_id;
    update user_by_stats set price_sum = total where user_id = user_id;
END $$
delimiter ;
```
> `NEW`: insert/update된 후 tuple을 가리킴.

## trigger를 정의할 때 알고있으면 좋은 내용1
update, insert, delete 등을 한번에 감지하도록 설정 가능
(MySQL은 불가능)
```sql
CREATE TRIGGER avg_empl_salary_trigger
AFTER INSERT OR UPDATE OR DELETE
ON employee FOR EACH ROW 
EXCUTE FUNCTION update_avg_empl_salary();
```
### FOR EACH ROW의 문제점
```sql
UPDATE employee SET salary = 1.5 * salary WHERE dept_id = 1003;
```
1003 부서에 임직원이 다섯명 있다면 `avg_empl_salary_trigger`는 다섯번 실행된다.
그런데 임직원 평균 연봉계산은 1번만 동작해도 괜찮다. 효율적으로 동작하게 하려면?
```sql
CREATE TRIGGER avg_empl_salary_trigger
AFTER INSERT OR UPDATE OR DELETE
ON employee FOR EACH STATEMENT 
EXCUTE FUNCTION update_avg_empl_salary();
```
1003 부서에 임직원이 다섯명 있어도 `avg_empl_salary_trigger`는 한번만 실행된다.

## trigger를 정의할 때 알고있으면 좋은 내용2
row단위가 아니라 statement단위로 trigger가 실행될 수 있도록 할 수 있다.
(MySQL은 불가능)

## trigger를 정의할 때 알고있으면 좋은 내용3
trigger를 발생시킬 디테일한 조건을 지정할 수 있다.
(MySQL은 불가능)
```sql
delimiter $$
CREATE TRIGGER log_user_nickname_trigger
BEFORE UPDATE 
ON users FOR EACH ROW
WHEN (NEW.nickname IS DISTINCT FROM OLD.nickname)
EXCUTE FUNCTION log_user_nickname();
```
## trigger 사용시 주의사항
1. 소스코드로는 발견할 수 없는 로직이기 때문에, 어떤 동작이 일어나는지 파악하기 어렵고 문제가 생겼을 때 대응하기 어렵다.
   ![스크린샷 2024-04-27 오후 12 15 09](https://github.com/f-lab-edu/modoospace/assets/48192141/72dd875a-2b8c-406e-9b58-9eb5ba5a0e6d)
2. 트리거가 너무 많이 동작하면 파악하기 힘들다.
   ![스크린샷 2024-04-27 오후 12 16 00](https://github.com/f-lab-edu/modoospace/assets/48192141/0c2d38a0-ec4c-427a-b55a-2222b3c04dc8)
3. 과도한 트리거 사용은 DB에 부담을 주고 응답을 느리게 만든다.
4. 디버깅이 어렵다.
5. 문서 정리가 특히나 중요하다.

> 트리거는 유지보수 관리가 힘들기 때문에 최대한 피하자.