package tobyspring.config;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyAutoConfiImportSelector implements DeferredImportSelector {

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return new String[]{
        "tobyspring.config.autoconfig.DispatcherServletConfig",
        "tobyspring.config.autoconfig.TomcatWebServerConfig"
    };
  }
}
