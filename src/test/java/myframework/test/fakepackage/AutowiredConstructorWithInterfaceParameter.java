package myframework.test.fakepackage;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class AutowiredConstructorWithInterfaceParameter {
    private final FakeInterface fakeInterface;

    @Autowired
    private AutowiredConstructorWithInterfaceParameter(FakeInterface fakeInterface) {
        this.fakeInterface = fakeInterface;
    }
}
