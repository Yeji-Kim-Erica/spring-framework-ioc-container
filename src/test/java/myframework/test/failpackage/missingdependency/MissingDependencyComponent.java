package myframework.test.failpackage.missingdependency;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class MissingDependencyComponent {
    @Autowired
    NonComponent nonComponent;
}
