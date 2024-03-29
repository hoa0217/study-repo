# Singleton Container
- 스프링 컨테이너는 싱글톤 패턴을 적용하지 않아도, 객체 인스턴스를 싱글톤으로 관리힌다.
- 스프링 컨테이너는 싱글톤 컨테이너 역할을하며, 싱글톤 객체를 생성하고 관리하는 기능을 싱글톤 레지스트리라 한다.
- 따라서 싱글톤 패턴의 모든 단점을 해결하면서 객체를 싱글톤으로 유지할 수 있다.
  - 싱글톤 패턴을 위한 지저분한 코드가 들어가지 않아도된다.
  - DIP, OCP, 테스트, private 생성자로부터 자유롭게 싱글톤을 사용할 수 있다.

<img width="800" alt="스크린샷 2024-01-08 오후 3 42 17" src="https://github.com/hoa0217/study-repo/assets/48192141/8d84a7aa-404e-480e-8780-bee7dcea3d9a">

- 만들어진 객체를 공유하여 효율적으로 재사용할 수 있음.

> 스프링의 기본 빈 등록 방식은 싱글톤이지만, 빈스코프로 이를 변경할 수 있다.
