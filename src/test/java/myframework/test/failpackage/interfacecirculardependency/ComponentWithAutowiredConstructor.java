package myframework.test.failpackage.interfacecirculardependency;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class ComponentWithAutowiredConstructor {
    private final Interface anInterface;

    @Autowired
    private ComponentWithAutowiredConstructor(Interface anInterface) {
        this.anInterface = anInterface;
    }
}
