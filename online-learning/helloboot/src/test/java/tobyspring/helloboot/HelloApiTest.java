package tobyspring.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class HelloApiTest {

  @Test
  void helloApi() {
    // http localhost:8080/hello?name=Spring
    // 제품명 : HTTPie

    // RestTemplate : 스프링에서 만든 클래스. api요청을 호출해서 응답을 가져올 수 있다.
    // 하지만 테스트목적으로 쓰기에는 살짝 불편하다.
    // 정상적으로 결과를 가져올때는 응답을 잘 돌려주는데 서버에 문제가 있어서 400~500에러가 날 경우  RestTemplate은 예외를 던진다.
    // 예외를 잡아서 검증하는 방법도있지만 그거보단 응답을 자체를 순수하게 가져와서 상태코드, content-type 등을 체크하고 싶을 땐 TestRestTemplate을 사용하자.

    TestRestTemplate rest = new TestRestTemplate();

    // getForEntity(url, responseType, urlVariables)
    ResponseEntity<String> res = rest
        .getForEntity("http://localhost:8080/hello?name={name}", String.class, "Spring");

    // status code 200
    Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

    // header(content-type) text/plain
    // "text/plain;charset=ISO-8859-1" : 바디가 영어로 올 땐 상관없지만, 한글이 들어갈 땐 인코딩이 중요하다. 지금은 앞부분만 검증해주자.
    Assertions.assertThat(res.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
        .startsWith(MediaType.TEXT_PLAIN_VALUE);

    // body Hello Spring
    // 응답값을 String으로 지정해놨기 때문에 String으로 비교해주면 된다.
    Assertions.assertThat(res.getBody()).isEqualTo("Hello Spring");

  }
}
