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
    TestRestTemplate rest = new TestRestTemplate();

    ResponseEntity<String> res = rest
        .getForEntity("http://localhost:8080/hello?name={name}", String.class, "Spring");

    Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

    Assertions.assertThat(res.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
        .startsWith(MediaType.TEXT_PLAIN_VALUE);

    Assertions.assertThat(res.getBody()).isEqualTo("Hello Spring");
  }

  @Test
  void failhelloApi() {
    TestRestTemplate rest = new TestRestTemplate();

    ResponseEntity<String> res = rest
        .getForEntity("http://localhost:8080/hello?name=", String.class);

    Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

}