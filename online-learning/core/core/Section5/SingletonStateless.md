# 싱글톤 방식 주의점
싱글톤 방식은 객체 인스턴스를 하나만 생성해서 공유하기 때문에 **무상태(stateless)**로 설계해야한다.
- 특정 클라이언트에 의존적인 필드가 있으면 안된다.
- 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다.
- 가급적 읽기만 가능해야한다.
- 필드 대신 공유되지 않는 지역변수, 파라미터, ThreadLocal 등을 사용해야한다.

> 스프링 빈의 필드에 공유 값을 설정하면 큰 장애가 발생할 수 있다.

### 예시
```java
public class StatefulService {

    private int price; // 상태를 유지하는 필드

    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        this.price = price; // 여기가 문제!
        return price;
    }

    public int getPrice() {
        return price;
    }
}
```

```java
class StatefulServiceTest {

    @Test
    void statefulServiceSingleton(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(
            TestConfig.class);

        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        // ThreadA : A사용자 10000원 주문
        int price = statefulService1.order("userA", 10000);
        // ThreadB : B사용자 20000원 주문
        statefulService2.order("userB", 20000);

        // ThreadA : A사용자 주문금액 조회
        int price = statefulService1.getPrice();
        
        assertThat(price).isEqualTo(10000); // 10000원을 기대했지만 기대와 다르게 20000원 출력
    }

    static class  TestConfig{
        @Bean
        public StatefulService statefulService(){
            return new StatefulService();
        }
    }

}
```
- price 필드가 공유되기 때문에 여러 클라이언트가 값을 변경할 수 있다.
  - 따라서 사용자 A의 주문금액이 10000원이 되어야하는데 20000원이 되버림.
- 공유필드는 조심해야하며, 스프링빈은 항상 무상태(stateless)로 설계하자. 
