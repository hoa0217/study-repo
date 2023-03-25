## 2. 자바와 절차적/구조적 프로그래밍

### 자바 프로그램의 개발과 구동
|현실세계         |가상 세계(자바 월드) ||
|---------|----------|----------|
|소프트웨어 개발 도구|JDK(Java Development Kit) - 자바 개발 도구|JVM용 소프트웨어 개발 도구|
|운영체제|JRE(Java Runtime Environment) - 자바 실행 환경|JVM용 OS|
|하드웨어 - 물리적 컴퓨터|JVM(Java Virtual Machine) - 자바 가상 기계|가상의 컴퓨터|
- JDK를 이용해 개발된 프로그램은 JRE에 의해 가상의 컴퓨터인 JVM상에서 구동된다.
  
    <img src="img/jvm_jre_jdk2.jpeg" width="80%"/>
  
    - JDK가 JRE를 포함하고 JRE는 JVM을 포함하는 형태로 배포된다.
    - 이로인해 개발자는 본인 OS에 맞는 JVM용으로 프로그램을 작성하고 배포하면 JVM이 중재자로서 각 OS에서 구동하는 데 아무 문제 없이 만들어준다.

- 객체 지향 프로그램의 메모리 사용방식

  <img src="img/t_memory.jpeg" width="80%"/>
  
  - 데이터 저장 영역 = T메모리 구조

    


    

