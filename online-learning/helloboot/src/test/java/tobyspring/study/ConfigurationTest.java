package tobyspring.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class ConfigurationTest {

  @Test
  void configuration(){
    MyConfig myConfig = new MyConfig();
    Bean1 bean1 = myConfig.bean1();
    Bean2 bean2 = myConfig.bean2();
    Assertions.assertThat(bean1.common).isNotEqualTo(bean2.common);
  }

  @Test
  void springConfiguration(){
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
    ac.register(MyConfig.class);
    ac.refresh();

    Bean1 containerBean1 = ac.getBean(Bean1.class);
    Bean2 containerBean2 = ac.getBean(Bean2.class);

    Assertions.assertThat(containerBean1.common).isEqualTo(containerBean2.common);
  }

  @Test
  void proxyCommonMethod(){
    MyConfigProxy myConfigProxy = new MyConfigProxy();

    Bean1 bean1 = myConfigProxy.bean1();
    Bean2 bean2 = myConfigProxy.bean2();

    Assertions.assertThat(bean1.common).isEqualTo(bean2.common);
  }

  static class MyConfigProxy extends MyConfig{

    private Common common;

    @Override
    Common common() {
      if(this.common == null) this.common = super.common();
      return this.common;
    }
  }

  //@Configuration(proxyBeanMethods = false)
  @Configuration
  static class MyConfig {
    @Bean
    Common common(){
      return new Common();
    }

    @Bean
    Bean1 bean1(){
      return new Bean1(common());
    }

    @Bean
    Bean2 bean2(){
      return new Bean2(common());
    }
  }

  static class Bean1{
    private final Common common;

    Bean1(Common common){
      this.common = common;
    }
  }

  static class Bean2{
    private final Common common;

    Bean2(Common common){
      this.common = common;
    }
  }

  static class Common {
    // Bean1 <-- Common
    // Bean2 <-- Common
  }
}
