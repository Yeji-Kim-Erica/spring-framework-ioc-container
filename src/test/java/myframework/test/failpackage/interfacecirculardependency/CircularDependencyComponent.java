package myframework.test.failpackage.interfacecirculardependency;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class CircularDependencyComponent implements Interface {
    private final ComponentWithAutowiredConstructor componentWithAutowiredConstructor;

    @Autowired
    private CircularDependencyComponent(ComponentWithAutowiredConstructor componentWithAutowiredConstructor) {
        this.componentWithAutowiredConstructor = componentWithAutowiredConstructor;
    }
}
