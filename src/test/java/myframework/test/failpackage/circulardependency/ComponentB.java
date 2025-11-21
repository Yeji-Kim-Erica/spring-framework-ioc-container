package myframework.test.failpackage.circulardependency;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class ComponentB {
    private final ComponentA componentA;

    @Autowired
    private ComponentB(ComponentA componentA) {
        this.componentA = componentA;
    }
}
