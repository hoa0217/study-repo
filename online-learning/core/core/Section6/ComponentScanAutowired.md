# 컴포넌트 스캔과 의존관계 자동 주입

- 직접 자바코드(`@Bean`)또는 XML(`<bean>`)에 설정정보를 등록하는건 귀찮은 일.
- 그래서 스프링엔 `@ComponentScan`, `@Autowired` 기능을 제공한다.

### @ComponentScan
```java
@Configuration
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

}
```
- `@ComponentScan`을 사용하면 `@Component`애노테이션이 붙은 클래스를 스캔해서 스프링빈으로 등록한다.

> 해당 코드에서는 기존 `@Configuration`이 붙은 설정정보 등록을 제외하기 위해 필터 사용.

```java
@Component
public class MemoryMemberRepository implements MemberRepository {
    ...    
}
```
<img width="800" alt="스크린샷 2024-01-16 오후 10 56 49" src="https://github.com/hoa0217/study-repo/assets/48192141/05847fcf-d161-46e2-954e-f17f8b33ebbb">

- 빈 이름 기본전력 : 맨 앞글자는 소문자인 기본 클래스명 사용
- 빈 이름 직접지정 : 지정하고 싶다면, `@Component("memberService2)` 이런식으로 부여

### @Autowired
@Bean으로 등록할 땐 의존관계를 직접 명시했지만 이제는 이런 설정 정보가 없기 때문에 클래스 안에서 해결해야한다.
```java
@Component
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    
    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {

        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    ...
}
```
<img width="800" alt="스크린샷 2024-01-16 오후 10 59 49" src="https://github.com/hoa0217/study-repo/assets/48192141/fbe42b53-a5d0-411c-a9d5-a931bafd4f86">

- 생성자에 `@AutoWired`를 지정하면, 스프링 컨테이너가 자동으로 해당 빈을 찾아서 주입한다.
- 기본조회는 타입이 같은 빈을 찾아서 주입힌다. `getBean(MemberRepository.class)`
  - 만약 타입이 겹치는 빈이 존재한다면? **에러발생.**

### 권장 방법
```java
@Configuration
@ComponentScan(
    basePackages = "hello.core.member", 
    excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

}
```

- basePackages를 통해 탐색할 패키지의 시작 위치를 지정할 수 있다. (디폴트는 해당 클래스 패키지)
- 하지만 basePackages를 두기보단, **설정 클래스를 프로젝트 시작 루트에 두는 것을 추천.**
- ex) 스프링부트 `@SpringBootApplication`는 프로젝트 시작 위치에 두는 것이 관례.
  - 그리고 이 안에 `@ComponentScan`이 들어있다.

