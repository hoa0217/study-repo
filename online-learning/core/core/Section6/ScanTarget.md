# 컴포넌트 스캔 기본 대상

- `@Component` : 컴포넌트 스캔에서 사용
- `@Controller` : 스프링 MVC 컨트롤러로 인식
- `@Service` : 스프링 비즈니스 로직에서 사용
  - 특별한 처리는 없으나, 개발자들은 이를 보고 비즈니스 계층이라는 것을 인식할 수 있음.  
- `@Repository` : 스프링 데이터 접근 계층으로 인식
  - **데이터 계층 예외를 스프링 예외로 변환해준다.**
  - 이를 통해 OCP를 지킬 수 있다.
  - DB마다 예외가 다르기 때문에 예외가 다른 계층으로 던져진다면 구체 클래스를 알게되는 것.
- `@Configuration` : 스프링 설정 정보에서 사용

해당 클래스 소스코드를 보면 `@Component`를 포함하고 있음
```java
@Component
public @interface Controller {
    
} 
```
```java
@Component
public @interface Service {
    
} 
```
```java
@Component
public @interface Configuration {
    
} 
```
> 참고 : 애노테이션에 상속관계라는 것은 없으나, 특정 애노테이션을 들고 있는 것을 인식할 수 있는 것은 스프링이 지원하는 기능이다. (자바가 지원하는 것이 아님!)

