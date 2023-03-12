package tobyspring.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyAutoConfiImportSelector implements DeferredImportSelector {

  private final ClassLoader classLoader;

  public MyAutoConfiImportSelector(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    List<String> autoConfigs = new ArrayList<>();

    ImportCandidates.load(MyAutoConfiguration.class, classLoader).forEach(autoConfigs::add);

    return autoConfigs.toArray(new String[0]);
  }
}
