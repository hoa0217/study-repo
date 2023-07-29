package study.datajpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DataJpaApplication {

  public static void main(String[] args) {
    SpringApplication.run(DataJpaApplication.class, args);
  }

  @Bean
  public AuditorAware<String> auditorProvider() {
    // 등록자, 수정자를 처리해주는 AuditorAware 스프링 빈 등록
    // 실무에서는 SpringSecurity 또는 세션으로 로그인한 사람 정보를 받아옴
    return () -> Optional.of(UUID.randomUUID().toString());
  }
}
