package myframework.test.fakepackage;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class ImplementationWithAutowiredConstructor implements InterfaceForAutowiredConstructor {
    private FakeAutowiredComponent autowiredComponent;

    @Autowired
    private ImplementationWithAutowiredConstructor(FakeAutowiredComponent autowiredComponent) {
        this.autowiredComponent = autowiredComponent;
    }
}
