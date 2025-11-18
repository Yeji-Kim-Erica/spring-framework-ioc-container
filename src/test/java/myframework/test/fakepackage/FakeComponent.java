package myframework.test.fakepackage;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class FakeComponent {
    @Autowired
    FakeAutowiredComponent autowiredComponent;
}
