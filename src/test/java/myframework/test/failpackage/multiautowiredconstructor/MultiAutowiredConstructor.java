package myframework.test.failpackage.multiautowiredconstructor;

import myframework.annotation.Autowired;
import myframework.annotation.Component;

@Component
public class MultiAutowiredConstructor {
    @Autowired
    public MultiAutowiredConstructor(DependencyA a) {}

    @Autowired
    public MultiAutowiredConstructor(DependencyB b) {}
}
