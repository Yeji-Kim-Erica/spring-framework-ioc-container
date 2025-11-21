package myframework.test.fakepackage;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class AutowiredConstructorWithInterfaceWithAutowiredConstructor {
    private final InterfaceForAutowiredConstructor component;

    @Autowired
    private AutowiredConstructorWithInterfaceWithAutowiredConstructor(InterfaceForAutowiredConstructor component) {
        this.component = component;
    }
}
