# 스프링 빈 설정 메타 정보 - BeanDefinition

- BeanDefinition : 빈 설정 메타정보
- `@Bean`, `<bean>` 당 각각 하나씩 메타정보가 생성된다.
- 스프링 컨테이너는 이 메타정보를 기반으로 스프링 빈을 생성한다.

<img width="800" alt="스크린샷 2024-01-04 오후 11 29 16" src="https://github.com/hoa0217/study-repo/assets/48192141/8741fa4d-401b-4b2b-94f7-070410376fe6">

- `AnnotaionConfigApplicationContext`는 `AnnotatedBeanDefinitionReader`를 사용해서 `AppConfig.class`를 읽고 `BeanDefinition`을 생성한다.
- 새로운 형식이 추가되면 `XXXApplicationContext`는 `XXXBeanDefinitionReader`를 사용해서 `AppConfig.xxx`를 읽고 `BeanDefinition`을 생성한다.

### BeanDefinition 정보

```bash
# xml 설정 
beanDefinition = Generic bean: class [hello.core.order.OrderServiceImpl];  
scope=; 
abstract=false; 
lazyInit=false; 
autowireMode=0; 
dependencyCheck=0; 
autowireCandidate=true; 
primary=false; 
factoryBeanName=null; 
factoryMethodName=null; 
initMethodNames=null; 
destroyMethodNames=null; defined in class path resource [appConfig.xml]

# class 설정
beanDefinition = Root bean: class [null];
scope=; 
abstract=false; 
lazyInit=null; 
autowireMode=3; 
dependencyCheck=0; 
autowireCandidate=true; 
primary=false; 
factoryBeanName=appConfig; 
factoryMethodName=orderService; 
initMethodNames=null; 
destroyMethodNames=[(inferred)]; defined in hello.core.AppConfig
```

- BeanClassName: 생성할 빈의 클래스 명(자바 설정 처럼 팩토리 역할의 빈을 사용하면 없음)
- Scope: 싱글톤(기본값)
- lazyInit: 스프링 컨테이너를 생성할 때 빈을 생성하는 것이 아니라, 실제 빈을 사용할 때 까지 최대한 생성을 지연 처리 하는지 여부
- factoryBeanName: 팩토리 역할의 빈을 사용할 경우 이름, 예) appConfig
- factoryMethodName: 빈을 생성할 팩토리 메서드 지정, 예) memberService
- InitMethodName: 빈을 생성하고, 의존관계를 적용한 뒤에 호출되는 초기화 메서드 명
- DestroyMethodName: 빈의 생명주기가 끝나서 제거하기 직전에 호출되는 메서드 명

> 스프링이 다양한 형태의 설정 정보를 BeanDefinition으로 추상화해서 사용하는 것 정도만 이해하자.
