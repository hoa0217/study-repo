# Unrecoverable

![스크린샷 2024-04-27 오후 4 20 14](https://github.com/hoa0217/study-repo/assets/48192141/bd83b68a-8d0a-46fb-a4ac-2f31322918d6)

## Unrecoverable Schedule
스케줄 내에서 commit된 transaction이 rollback된 transaction이 write 했었던 데이터를 읽은 경우
rollback을 해도 이전 상태로 회복 불가능할 수 있기 때문에, 이런 Schedule은 DBMS가 허용하면 안된다.
- 이 예제에서는 2번 트랜잭션의 데이터를 읽은 1번 트랜잭션이 커밋되었는데 그 후 2번이 롤백된 상태
- 1번 트랜잭션 유효하지 않은 작업을 읽어서 처리한 셈이됨.

그러면 어떤 schedule이 recoverable한가?

## Recoverable Schedule
![스크린샷 2024-04-27 오후 4 25 37](https://github.com/hoa0217/study-repo/assets/48192141/900d5e11-ec03-48ee-95ab-e3f0fc7c1452)

rollback할 때 이전 상태로 온전히 돌아갈 수 있기 때문에 DBMS는 이런 Schedule만 허용해야한다.
- 2번 트랜잭션이 커밋된 후 1번도 커밋해야한다.
- 만약 2번 트랜잭션이 롤백되면, 2번 트랜잭션에 의존성을 가지고있는 1번 트랜잭션도 롤백됨. ➡️ Cascading Rollback

## Cascading Rollback
여러 transaction의 rollback이 연쇄적으로 일어나면 처리하는 비용이 많이든다.

이 문제를 어떠헥 해결할 수 있을까?
- 데이터를 write한 transaction이 commit/rollback한 뒤 데이터를 읽는 schedule을 허용하자!

## Cascadeless Schedule (avoid cascading rollback)
![스크린샷 2024-04-27 오후 4 31 37](https://github.com/hoa0217/study-repo/assets/48192141/3a9a3746-62bb-43af-b08d-5e3480ee9e52)
스케줄 내에서 어떤 트랜잭션도 commit되지 않은 트랜잭션들이 write한 데이터를 읽지 않는 경우 

하지만 cascadeless Schedule이어도 문제가 발생할 수 있다.

![스크린샷 2024-04-27 오후 4 33 20](https://github.com/hoa0217/study-repo/assets/48192141/35d2cc2a-4dd3-42e5-8bf0-122d0710c090)
> 3만원-> 1만원 -> 2만원으로 바꿧으나 1만원 트랜잭션이 롤백될경우? 피자는 다시 3만원이 된다.

추가적인 보강이 필요하다.

즉, commit되지 않은 트랜잭션이 write한 데이터는 쓰지도 읽지도 않아야한다. ➡️ strict schedule

## Strict Schedule
![스크린샷 2024-04-27 오후 4 36 02](https://github.com/hoa0217/study-repo/assets/48192141/23c3069e-8665-40c7-9aac-5568628ddf38)

이는 롤백할 때 recovery가 쉽다. transaction 이전 상태로 돌려놓기만 하면 된다.

## 최종정리
![스크린샷 2024-04-27 오후 4 38 12](https://github.com/hoa0217/study-repo/assets/48192141/2888ccf6-b94f-4c98-a4eb-83a88c7700c0)

![스크린샷 2024-04-27 오후 4 38 31](https://github.com/hoa0217/study-repo/assets/48192141/31898614-eaff-4d97-bc9f-4b061f29f217)




