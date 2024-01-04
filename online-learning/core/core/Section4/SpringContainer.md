# 스프링컨테이너

```java
ApplicationContext applicationContext = 
    new AnnotationConfigApplicationContext(AppConfig.class);
```

- ApplicationContext : 스프링 컨테이너
    - 이는 **XML**로도 만들 수 있고 **애노테이션 기반의 자바 설정 클래스**로도 만들 수 있다.
    - 주로 자바 설정 클래스로 만듬.
- AnnotationConfigApplicationContext는 ApplicationContext의 구현체이다.

> 스프링 컨테이너를 부를 때 BeanFactory, ApplicationContext로 구분해서 이야기한다.
> BeanFactory를 사용하는 경우는 거의 없으므로 일반적으로 ApplicationContext를 스프링 컨테이너라 한다.

### 스프링 컨테이너 생성과정
1. 스프링 컨테이너 생성

<img width="700" alt="스크린샷 2024-01-03 오후 12 18 05" src="https://github.com/hoa0217/study-repo/assets/48192141/1fddabd4-f6a9-4ee6-b89c-e69c36b854d8">

- 스프링 컨테이너를 생성할 때 구성 정보(`AppConfig.class`)를 지정해준다.

2. 스프링 빈 등록

<img width="700" alt="스크린샷 2024-01-03 오후 12 19 15" src="https://github.com/hoa0217/study-repo/assets/48192141/3bf34cab-c4db-4b99-8342-0cb430bcb0d8">

- 파라미터로 넘어온 설정 클래스 정보를 사용해 스프링 빈을 등록한다.

> 빈이름 : 빈이름은 메서드 이름을 사용하며, 직접 부여할 수 도있다.
> 
> ```java
> @Bean(name="memberService2")
> ```
> 
> 하지만, 빈이름은 항상 다른 이름을 부여해야한다. (오류 발생)

3. 스프링 빈 의존관계 설정

<img width="700" alt="스크린샷 2024-01-03 오후 12 22 28" src="https://github.com/hoa0217/study-repo/assets/48192141/63ffb0a9-7299-4173-94c6-424856cbe9cd">

- 설정 정보를 활용해, 빈을 생성하고 의존관계를 주입(DI)한다.

### BeanFactory와 ApplicationContext

```
BeanFactory(interface) ⬅️ ApplicationContext(interface) ⬅️ AnnotationConfigApplicationContext
```

BeanFactory
- 스프링 컨테이너 최상위 인터페이스
- 스프링 빈을 관리하고 조회하는 역할 담당 `getBean()`

ApplicationContext
- BeanFactory 기능 상속 + 부가 기능 제공
> Beanfactory는 직접사용할 일이 없고, ApplicationContext를 사용한다.

ApplicationContext 부가기능

<img width="700" alt="스크린샷 2024-01-04 오후 10 42 35" src="https://github.com/hoa0217/study-repo/assets/48192141/7a67f16c-b664-4c3b-9c8e-0c5e8af67278">

- MessageSource : 국제화 기능
- EnvironmentCapable : 환경변수 (로컬, 개발, 운영 등 환경을 구분해서 처리)
- ApplicationEventPublisher : 어플리케이션 이벤트 (이벤트 발행 및 구독)
- ResourceLoader : 리소스 조회





