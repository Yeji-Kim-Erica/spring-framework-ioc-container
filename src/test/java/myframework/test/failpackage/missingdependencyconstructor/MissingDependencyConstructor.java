package myframework.test.failpackage.missingdependencyconstructor;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class MissingDependencyConstructor {
    private final NonComponent nonComponent;

    @Autowired
    public MissingDependencyConstructor(NonComponent nonComponent) {
        this.nonComponent = nonComponent;
    }
}
