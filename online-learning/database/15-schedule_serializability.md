# schedule과 serializability

![스크린샷 2024-04-27 오후 1 11 00](https://github.com/f-lab-edu/modoospace/assets/48192141/ed966e6e-b81d-4daf-bf69-7187e9bfda68)
> Lost Update: 중간 트랜잭션 결과가 유실되는 것

하나하나의 실행을 operation으로 보고 간소화 시켜보자.

![스크린샷 2024-04-27 오후 1 13 01](https://github.com/f-lab-edu/modoospace/assets/48192141/16a35bf2-705e-47aa-8b6f-a9c7f2cb83d5)

모든 케이스를 간소화 시켜서 가로로 나타내면 아래와 같다.

![스크린샷 2024-04-27 오후 1 13 42](https://github.com/f-lab-edu/modoospace/assets/48192141/1e56f004-223a-4e6d-9691-a51c79e4c02c)

이 실행 순서들이 바로 Schedule이다.

## Schedule

![스크린샷 2024-04-27 오후 1 30 03](https://github.com/f-lab-edu/modoospace/assets/48192141/0c65cbe9-215c-48f6-af39-10108c3a86c5)

Schedule: 여러 transaction들이 동시에 실행될 때 각 transaction에 속한 operation들의 실행 순서. 
 각 transaction 내의 opertaion들의 순서는 바뀌지 않는다.
- Serial Schedule: transaction들이 겹치지 않고 한번에 하나씩 실행되는 스케줄. ex) 1번, 2번
  - 한번에 하나의 transaction만 실행되기 때문에 좋은 성능을 낼 수 없고 현실적으로 사용할 수 없는 방식이다.
- Nonserial Schedule: transaction들이 겹쳐셔(interleaving)실행되는 스케줄 ex) 3번, 4번
  - transaction들이 겹쳐서 실행되기 때문에 동시성이 높아져서 같은 시간동안 더 많은 transaction들을 처리할 수 있다.

![스크린샷 2024-04-27 오후 1 37 10](https://github.com/f-lab-edu/modoospace/assets/48192141/cdfe0aab-e3a8-42a0-a442-09cf6354e4d5)


## Nonserial Schedule 단점과 해결방법
![스크린샷 2024-04-27 오후 1 38 35](https://github.com/f-lab-edu/modoospace/assets/48192141/d20d2be7-d571-4625-bf4f-cf263954a659)
transaction들이 어떤 형태로 겹쳐서 실행되는지에 따라 이상한 결과가 나올 수 있다.

![스크린샷 2024-04-27 오후 1 39 21](https://github.com/f-lab-edu/modoospace/assets/48192141/69f77118-585e-40cd-8144-1bb85bc85a07)

## Conflict of two opertaions
세가지 조건을 모두 만족하면 conflict
1. 서로 다른 transaction 소속
2. 같은 데이터에 접근
3. 최소 하나는 write operation

![스크린샷 2024-04-27 오후 1 41 07](https://github.com/f-lab-edu/modoospace/assets/48192141/372b8c6e-bf50-44da-b96d-b258fe9de929)

> 스케줄3번은 총 3개의 Conflict가 존재한다. read-write, write-write

### 왜 이 Conflict가 중요한가?
![스크린샷 2024-04-27 오후 1 43 10](https://github.com/f-lab-edu/modoospace/assets/48192141/803c8507-b15b-420c-8537-07108444b7e6)
conflict opertation은 순서가 바뀌면 결과도 바뀐다.

## Conflict equibalent for two schedules
두 조건 모두 만족하는 conflict equivalent
1. 두 schedule은 같은 transaction들을 가진다.
2. 어떤 conflicting operations의 순서도 양쪽 schedule모두 동일하다.

![스크린샷 2024-04-27 오후 1 45 39](https://github.com/f-lab-edu/modoospace/assets/48192141/0e914f9e-7ede-4619-8d01-5053ead080af)

- 이때 스케줄2번은 시리얼 스케줄이다.
- 스케줄3번은 시리얼 스케줄과 conflict equivalent이다. ➡️ conflict serializable
- 결국 스케줄3번은 conflict serializable
- 그렇기 때문에 정상적인 결과가 나온다.

![스크린샷 2024-04-27 오후 1 47 51](https://github.com/f-lab-edu/modoospace/assets/48192141/9eebbeb2-59e0-47cf-a0ce-02e47b9c3419)

- 스케줄4번은 시리얼 스케줄과 비교했을 때 순서가 역전되어있으므로 conflict equivalent하지 않다. 
- 그 어떤 시리얼 스케줄과도 conflict equivalent하지 않기때문에 NOT conflict serializable하다.

## 다음 시간에..
![스크린샷 2024-04-27 오후 1 50 13](https://github.com/f-lab-edu/modoospace/assets/48192141/76d2c1db-5a67-4800-ab7c-b244cad483b9)
![스크린샷 2024-04-27 오후 1 50 40](https://github.com/f-lab-edu/modoospace/assets/48192141/1d607e06-3ce0-48be-a466-b79b748a4a4a)


