## 최범균의 JSP2.3 웹프로그래밍 기초부터 중급까지
### URL과 웹페이지
- URL(Uniform Resource Locator) : 웹브라우저 주소줄에 입력하는 일종의 주소
    - <img src="./img/url.jpeg" width="70%">
- 웹 페이지(web page) : 웹브라우저에 출력된 내용
### 웹 브라우저와 웹서버
<img src="./img/web.jpeg" width="70%">

- 웹브라우저에 URL을 입력하면 웹 서버가 웹 브라우저에게 웹페이지를 제공한다.
  - 요청(request) : 웹브라우저가 웹서버에 웹페이지를 달라고하는 것
  - 응답(response) : 요청한 웹페이지를 웹 브라우저에 제공하는 것
- 웹브라우저가 웹서버에 연결하려면, 웹서버의 컴퓨터 주소를 알아야하며 이를 IP주소라고 부른다.
- DNS(Domain Name Server) : 웹브라우저에 URL을 입력하면, DNS가 해당하는 IP 주소를 응답한다.

> 클라이언트(client) : 프로그램에서 요청하는 쪽 :arrow_right: 웹브라우저   
> 서버(server) : 요청을 받아 알맞은 기능이나 데이터를 제공하는 쪽 :arrow_right: 웹서버

- 또한 하나의 컴퓨터는 여러개의 웹 서버를 실행할 수 있기 때문에 **포트(port)** 번호로 연결한다.   
`http://localhost:8080/jsp_war_exploded/` (기본포트 80)
### HTML 과 HTTP
- 렌더링(rendering) : HTML 표준에 따라 알맞은 화면을 생성하는 과정
- HTTP(HyperText Transfer Protocol) : 웹브라우저와 웹 서버가 HTML을 비롯해 이미지, 동영상 등 다양한 데이터를 주고받을 때 사용하는 일종의 규칙 
- <img src="./img/http.jpeg" width="70%">
  
  - 요청 데이터 : 웹브라우저가 웹 서버로부터 무엇을 받고싶은지 기술
  - 응답 데이터 : 웹서버는 요청 데이터에 기술한 정보를 이용해 요청한 것을 응답 데이터에 담아 보냄
  > #### 웹서비스의 발전
  > 1세대 : `정적` HTML(+CSS)파일을 받아오는 형식
  > - user interaction이 많이 요구되지 않음
  > 
  > 2세대 : `동적` HTML + javascript를 통해 필요한 정보를 주고 받음 
  > - user interaction이 요구되기 시작
  > 
  > 3세대 : `SPA(Single Page Application)` 단일의 HTML페이지에서 전체 웹사이트/서비스를 구현한다. 이전에는 페이지 구성에 필요한 요소를 매번 전송했다면 3세대는 필요한 파일을 한번에 다운받고, 그뒤로 실시간 데이터만 주고받음.
  > - HTML/javascript(`FrontEnd`), 데이터개발(`BackEnd`)가 명확히 나뉨
  
  출처 : [웹 서비스의 역사와 발전](https://velog.io/@haleyjun/%EC%9B%B9-%EC%84%9C%EB%B9%84%EC%8A%A4%EC%9D%98-%EC%97%AD%EC%82%AC%EC%99%80-%EB%B0%9C%EC%A0%84-%EA%B0%84%EB%8B%A8%ED%9E%88)
### 정적 자원과 동적 자원
- 정적 자원 : 이미지, HTML 파일 등 자주 바뀌지 않는 자원 (동일 URL :arrow_right: 동일 파일 응답)
- 동적 자원(동적 페이지) : 시간이나 특정 조건에 따라 응답데이터가 달라지는 자원

### 웹 프로그래밍과 JSP
- 웹 프로그래밍 : 웹 서버가 웹 브라우저에 응답을 전송할 데이터를 생성해주는 프로그램을 작성하는 것
  - 간단히 말해, 웹 서버가 실행하는 프로그램을 만드는 것
- JSP(JavaServer Pages) : 동적페이지를 작성하는데 사용되는 자바 표준 기술(주로 HTML 응답 생성 목적)
  - JSP를 이용해서 만든 프로그램을 실행하려면 `Tomcat` 등의 서버 프로그램(WAS)이 필요함
- WAS(Web Application Server) : 웹을 위한 연결, 프로그래밍 언어, DB연동과 같이 어플리케이션을 구현하는데 필요한 기능을 제공한다.
  - 웹브라우저로부터 요청이 오면 알맞은 프로그램을 찾아 실행하고 응답을 전송함.   
  <img src="./img/was.jpeg" width="70%">

> 프로젝트 환경설정은 [IntelliJ IDEA로 JSP 프로젝트 생성](https://parkgang.github.io/series/from-jsp-project-setup-to-deployment/create-jsp-project-with-intellij-idea/) 을 참고했다.

<hr/>

### MIME
Multipurpose Internet Mail Extensions의 약자로, 이메일의 내용을 설명하기 위해 정의되었다. 하지만 메일 뿐만 아니라 HTTP등의 프로토콜에서도 응답 데이터의 내용을 설명하기 위해 사용한다. JSP에선 page 디렉티브의 contentType의 속성으로 MIME 타입이 사용된다.
- ex) text/html, text/xml, application/json   
- ```<% page contentType="text/html; charset=utf-8"%>```
> 만약 response 시 클라이언트의 동작이 이상하다면, mimetype설정을 확인해보자.

<hr/>

### Session
- 세션은 웹브라우저가 아닌 서버(웹 컨테이너)에 정보를 저장한다.
- 기본적으로 한 웹 브라우저마다 한 세션을 생성한다.
  - 같은 JSP페이지라도 웹 브라우저에 따라 서로 다른 세션을 사용한다.
  - 이 때 각 세션을 구분하기 위해 세션마다 **고유ID**를 할당한다 :arrow_right: 세션ID
- 서버에 웹브라우저가 처음 접속할 때 세션을 생성하고 이후 서버에 접속할 때마다 이미 생성된 세션ID를 보낸다.
  - 이를 통해 웹서버가 어떤 세션을 사용할지 판단할 수 있다.
> 서버와 브라우저간 이 세션ID를 공유하는 방법이 바로 **쿠키**다.

- 한 번 생성된 세션은 지정한 유효 시간 동안 유지된다.
  - 웹 어플리케이션을 실행하는 동안 지속적으로 사용해야 하는 데이터의 저장소로 세션이 적당하다.
- request 기본객체가 하나의 요청을 처리하는데 사용되는 JSP페이지 사이에서 공유된다면, session 기본객체는 웹브라우저의 여러 요청을 처리하는 JSP페이지 사이에서 공유된다.
  - 이는 웹 브라우저와 일대일로 관련된 값을 저장할 때 쿠키 대신 세션을 사용할 수 있다.
> 쿠키 vs 세션   
> 1. 세션은 쿠키보다 **보안**에서 앞선다.   
     쿠키의 이름이나 데이터는 네트워크를 통해 전달되기 때문에 HTTP프로토콜을 사용하는 경우 누군가 쿠키의 값을 읽어올 수 있다. 하지만 세션값은 오직 서버에만 저장된다.   
> 2. 웹 브라우저가 쿠키를 지원하지 않는 또는 막는 경우, 세션은 **쿠키 설정 여부에 상관없이 사용**할 수 있다.   
     서블릿/JSP는 쿠키를 사용할 수 없는 경우, URL 재작성 방식을 사용해서 세션ID를 공유할 수 있다. 
> 3. 하지만 **세션은 여러 서버에서 공유할 수 없다.**   
     `www.naver.com`의 세션과 `mail.naver.com`의 세션은 다르다.
> 반면 쿠키는 도메인을 이용해서 **쿠키를 여러 도메인 주소에 공유할 수 있다**. 이런 이유로 포털 사이트는 쿠키를 이용해 로그인 정보를 저장한다.

- 세션은 마지막 접근 이후 일정 시간 이내에 다시 세션에 접근하지 않는 경우 자동으로 세션을 종료한다.
  - 유효 시간 설정
    1. `WEB-INF\web.xml`파일의 `<session-config>` (분 단위)
    ```xml
    <session-config>
        <session-timeout>50</session-timeout>
    </session-config>
    ```
    2. `session.setMaxInactiveInterval(60*60)` (초 단위)
> **(주의)** 유효시간이 없는 상태에서 invalidate()메서드를 실행하지 않는 경우, 세션 객체가 계속 메모리에 남게되어 메모리 부족 현상이 발생할 수 있음.
  
- `request.getSession()` : session이 존재하면 해당 session을 리턴하고 존재하지 않으면 새롭게 session을 생성해서 리턴
- `request.getSession(false)` : 세션이 존재하는 경우만 session 객체를 리턴, 존재하지 않으면 null을 리턴







