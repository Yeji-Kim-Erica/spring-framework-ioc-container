package myframework.test.failpackage.circulardependency;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class ComponentA {
    private final ComponentB componentB;

    @Autowired
    private ComponentA(ComponentB componentB) {
        this.componentB = componentB;
    }
}
