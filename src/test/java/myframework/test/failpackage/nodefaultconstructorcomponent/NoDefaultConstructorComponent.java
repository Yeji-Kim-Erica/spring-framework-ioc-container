package myframework.test.failpackage.nodefaultconstructorcomponent;

import myframework.annotation.Component;

@Component
public class NoDefaultConstructorComponent {
    private final String parameter;

    public NoDefaultConstructorComponent(String parameter) {
        this.parameter = parameter;
    }
}
