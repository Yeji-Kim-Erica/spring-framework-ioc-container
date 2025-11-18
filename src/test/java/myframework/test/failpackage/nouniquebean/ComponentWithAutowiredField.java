package myframework.test.failpackage.nouniquebean;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class ComponentWithAutowiredField {
    @Autowired
    InterfaceForAB interfaceForAB;
}
