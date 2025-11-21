package myframework.test.failpackage.selfdependency;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class SelfDependency {
    private final SelfDependency selfDependency;

    @Autowired
    public SelfDependency(SelfDependency selfDependency) {
        this.selfDependency = selfDependency;
    }
}
