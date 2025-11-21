package myframework.test.fakepackage;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class FakeComponentWithAutowiredConstructor {
    private final FakeAutowiredComponent autowiredComponent;

    @Autowired
    private FakeComponentWithAutowiredConstructor(FakeAutowiredComponent autowiredComponent) {
        this.autowiredComponent = autowiredComponent;
    }
}
