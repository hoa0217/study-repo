## 도커란?
- 컨테이너를 사용하여 응용프로그램을 더 쉽게 만들고, 배포하고 실행할 수 있도록 설계된 컨테이너 기반의 오픈소스 가상화 플랫폼
- 일반 컨테이너 개념에서 물건을 손쉽게 운송해주는 것 처럼 **어플리케이션 환경에 구애받지 않고 손쉽게 배포 관리를 할 수 있게 해줌.**
- 컨테이너 기반 배포 방식은 구글을 비롯해 대부분 서비스 회사가 컨테이너로 서비스 운영 중

### 도커를 왜 사용해야할까?
- 똑같은 2대의 서버가 있다 해도, A서버는 1년전 구성했고 B서버는 막 구성했다면 OS부터 컴파일러, 설치된 패키지까지 완벽하게 같기는 쉽지 않다.
- 이러한 차이가 문제를 발생 시키고 장애가 날 수 도있다.
> A서버는 되는데 B서버는 왜 안되지?
- 도커는 서버마다 동일한 환경을 구성해주기 때문에 이러한 문제를 해결할 수 있다.
- 동일한 환경을 구성하기 때문에 auto scaling에 유리
  - 서버를 늘리고 줄일 때 자동으로 동일한 환경을 구성할 수 있음.

## 도커와 기존 가상화기술(VM)차이

<img width="737" alt="스크린샷 2024-02-28 오후 11 44 03" src="https://github.com/hoa0217/study-repo/assets/48192141/9f264bc0-e5ef-48fd-b1d6-41c99ed2fc32">

- 전통적 방식 : 한대의 서버에 하나의 어플리케이션만 운영하는 방식
  - 만약 여러개의 어플리케이션을 올리게 되면 OS를 공유하기때문에 문제가 전파될 가능성이 큼.
- VM : 하이퍼바이저라는 가상화 개념. 이는 호스트 시스템(윈도우, 리눅스 등)에서 다수의 게스트(OS)를 구동할 수 있게 해주는 SW.
  - 각 VM마다 독립적으로 동작한다.
  - 하지만 조금 무겁고 느리다는 단점
- Docker : 하이퍼바이저 구조를 토대로 등장했으나 VM과 달리 레이어가 적음. 
  - 훨씬 가볍게 동작하기때문에 성능에 유리하다.

> 전통적 방식보다 VM, Docker가 더 안전한 이유 : 어플리케이션이 논리적으로 구분이 되어있다. 따라서 하나의 어플리케이션에서 장애가 발생해도 다른 어플리케이션은 영향을 받지 않는다.

## 도커의 컨테이너와 이미지
- 이미지 : 코드, 런타임, 시스템 도구, 시스템 라이브러리 및 설정과 같은 응용프로그램을 실행 하는데 필요한 모든 것을 포함하는 패키지
  - 이는 Github와 유사하게 Dockerhub에서 버전관리가 가능하다.
- 컨테이너 : 도커 이미지를 독립된 공간에서 실행할 수 있게 해주는 기술

> 즉, 이미지는 프로그램을 실행하는데 필요한 설정이나 종속성들을 가지고 있다. 컨테이너는 이미지 인스턴스이며, 프로그램을 실행한다.

## 도커 파일 이란?

- Dockerfile이란 도커 이미지를 구성하기 위해 있어야할 패키지, 의존성, 소스코드 등을 하나의 file로 기록하여 이미지화 시킬 **명령 파일**
- 즉, 이미지는 컨테이너를 실행하기 위한 모든 정보를 가지고 있기 때문에 더 이상 새로운 서버가 추가되면 의존성 파일을 컴파일하고 이것 저것 설치할 필요가 없다.

<img width="914" alt="스크린샷 2024-02-28 오후 11 54 29" src="https://github.com/hoa0217/study-repo/assets/48192141/04409995-0eea-47fa-b0a6-2cd913d10651">

- `FROM` : 새로운 이미지를 생성할 때 기반으로 사용할 이미지 지정
- `ARG` : 이미지 빌드 시점에서 사용할 변수 지정
- `COPY` : 호스트에 있는 파일이나 디렉토리를 Docker 이미지의 파일 시스템으로 복사
- `ENV` : 환경 변수 지정
- `ENTRYPOINT` : 컨테이너가 실행되었을 때 항상 실행되어야하는 커맨드 지정

> 기본적으로 docker 컨테이너 시간은 UTC+0으로 설정되어있이때문에 한국시간(Asia/Seoul)으로 변경해야함.

## 도커 컴포즈란?
- Docker Compose란 **멀티컨테이너 도커 어플리케이션**을 정의하고 실행하는 도구
- Application, Database, Redis, Nginx 등 각 독립적인 컨테이너로 관리한다고 했을 때, 다중 컨테이너 라이프 사이클을 어떻게 관리해야할까?
- 여러개의 도커 컨테이너로 부터 이루어진 서비스를 구축 및 네트워크 연결, 실행순서를 자동으로 관리
- docker-compose.yml 파일을 작성하여 1회 실행하는 것으로 설정된 모든 컨테이너를 실행

![스크린샷 2024-02-29 오전 1 14 07](https://github.com/hoa0217/study-repo/assets/48192141/24cce0ce-c3a7-48fc-9b33-c8d3018c313e)

> docker 버전에 맞게 compose 버전을 명시하면된다. https://docs.docker.com/compose/compose-file/compose-versioning/


- .env 파일은 docker-compose 빌드 시 자동으로 환경변수가 주입된다.
```yaml
version: "3.8"
services:
  pharmacy-recommendation-redis:
    container_name: pharmacy-recommendation-redis
    build:
      dockerfile: Dockerfile
      context: ./redis
    image: jeonghwaheo/pharmacy-recommendation-redis
    ports:
      - "6379:6379"
  pharmacy-recommendation-database:
    container_name: pharmacy-recommendation-database
    build:
      dockerfile: Dockerfile
      context: ./database
    image: jeonghwaheo/pharmacy-recommendation-database
    environment:
      - MARIADB_DATABASE=pharmacy-recommendation
      - MARIADB_ROOT_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
    volumes:
      - ./database/config:/etc/mysql/conf.d # 한글깨짐현상으로 해당 config volume 연결
    ports:
      - "3306:3306"
```

> .env파일은 git에 올리면 안됨.

실행
```bash
docker-compose -f docker-compose-local.yml up
```
중지 및 컨테이너 정리
```bash
docker-compose -f docker-compose-local.yml down
```