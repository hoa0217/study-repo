package tobyspring.helloboot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME) // 언제까지 살아있을 것인가.
@Target(ElementType.TYPE) // 애노테이션을 적용할 대상을 지정.
@Component // 애노테이션의 메타애노테이션으로 @Component 사용
public @interface MyComponent {

}
