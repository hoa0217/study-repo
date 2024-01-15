# @Contiguration과 싱글톤
AppConfig 코드를 보면 메서드 호출 시 계속해서 객체를 새로 생성하는 것을 볼 수 있다.
```java
@Configuration
public class AppConfig {
    
    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
```
MemberRepository의 경우
- memberService() 호출 시 1번
- orderService() 호출 시 1번
- memberRepository() 호출 시 1번

얼핏보면 싱글톤이 깨지는 것 처럼 보인다. 하지만 이들은 스프링 컨테이너에서 관리되는 스프링 Bean들이다.

### @Contiguration과 바이트코드 조작
스프링 컨테이너는 싱글톤 레지스트리기 때문에 스프링 Bean이 싱글톤이 되도록 보장해주어야하지만, 자바 코드까지 어떻게 하기 어렵다.
- 자바 코드상으로는 분명 memberRepository가 3번 호출됨.

아래 코드를 통해 AppConfig Bean을 컨테이너에서 꺼내보자.
> AnnotationConfigApplicationContext 파라미터로 넘긴값도 스프링빈으로 등록된다.
```java
@Test
void configurationDeep() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(
        AppConfig.class);
    AppConfig bean = ac.getBean(AppConfig.class);

    System.out.println("bean = " + bean);
}
```
```bash
bean = hello.core.AppConfig$$SpringCGLIB$$0@68fa0ba8
```
출력해서보면, 빈에 등록된건 순수한 클래스가 아닌 CGLIB(바이트코드 조작 라이브러리)를 사용하여 AppConfig클래스를 상속받은 임의의 다른 클래스이다.

<img width="800" alt="스크린샷 2024-01-15 오후 11 35 44" src="https://github.com/hoa0217/study-repo/assets/48192141/addb9ffd-2c32-4f30-bc79-770b97993db1">

이 임의의 클래스가 바로 싱글톤이 되도록 보장해준다. (아래코드는 짐작으로 작성된 코드)

```java
@Bean
public MemberRepository memberRepository() {
    if (memoryMemberRepository가 이미 스프링 컨테이너에 등록되어 있으면?) { 
        return 스프링 컨테이너에서 찾아서 반환;
    } 
    else { //스프링 컨테이너에 없으면
        기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록 
        return 반환
    }
}
```

#### @Contiguration없이 @Bean만 등록한다면?
컨테이너에 GCLIB 기술없이 순수한 AppConfig가 등록되고, 객체도 여러번 등록된다. (싱글톤 깨짐.)

따라서 스프링 설정 정보는 항상 @Configuration을 사용해야한다.
