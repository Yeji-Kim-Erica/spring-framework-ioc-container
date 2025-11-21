package myframework.container;

import myframework.annotation.Autowired;
import myframework.annotation.Component;
import myframework.exception.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private static final String CLASS_FILE_NAME_SUFFIX = ".class";
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String FILE_PATH_SEPARATOR = "/";

    private final String BASE_PACKAGE;
    private final Map<String, Object> beans;
    private final Set<Class<?>> candidateComponents;

    public ApplicationContext(String basePackage) {
        this.BASE_PACKAGE = basePackage;
        this.beans = new ConcurrentHashMap<>();
        Set<Class<?>> candidateComponents = scanCandidateComponentsAndInstantiate();
        this.candidateComponents = Collections.unmodifiableSet(candidateComponents);
        injectDependencies();
    }

    public Object getBean(String beanName) {
        Object bean = beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanException(ErrorMessage.BEAN_NOT_FOUND.getMessage(beanName));
        }
        return bean;
    }

    public <T> T getBean(String beanName, Class<T> classType) {
        Object bean = getBean(beanName);
        if (classType.isInstance(bean)) {
            return classType.cast(bean);
        }
        String expected = classType.getName();
        String found = bean.getClass().getName();
        throw new NoSuchBeanException(ErrorMessage.BEAN_TYPE_NOT_MATCHED.getMessage(beanName, expected, found));
    }

    private Set<Class<?>> scanCandidateComponentsAndInstantiate() {
        Set<String> classNames = getClassNamesFromPackage(BASE_PACKAGE);
        Set<Class<?>> annotatedComponents = new HashSet<>();
        for (String className : classNames) {
            Class<?> clazz = putBeanIfAnnotatedAsComponent(className);
            if (clazz != null) {
                annotatedComponents.add(clazz);
            }
        }
        return annotatedComponents;
    }

    private Set<String> getClassNamesFromPackage(String basePackage) {
        String basePackagePath = basePackage.replace(PACKAGE_SEPARATOR, FILE_PATH_SEPARATOR);
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(basePackagePath);
            return getClassNamesFromResources(resources);
        } catch (IOException e) {
            throw new ComponentScanException(ErrorMessage.PACKAGE_SCAN_FAILED.getMessage(basePackage), e);
        }
    }

    private Set<String> getClassNamesFromResources(Enumeration<URL> resources) {
        List<File> files = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            files.add(new File(resource.getFile()));
        }
        return getClassNamesFromFiles(files);
    }

    private Set<String> getClassNamesFromFiles(List<File> files) {
        Set<String> classNames = new HashSet<>();
        for (File file : files) {
            findClassesFromDirectory(BASE_PACKAGE, file, classNames);
        }
        return classNames;
    }

    private void findClassesFromDirectory(String basePackage, File directory, Set<String> classNames) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            addClassNames(basePackage, file, classNames);
        }
    }

    private void addClassNames(String packagePrefix, File file, Set<String> classNames) {
        if (file.isDirectory()) {
            String subPackageName = file.getName();
            String newPackagePrefix = packagePrefix + PACKAGE_SEPARATOR + subPackageName;
            findClassesFromDirectory(newPackagePrefix, file, classNames);
            return;
        }

        String fileName = file.getName();
        if (fileName.endsWith(CLASS_FILE_NAME_SUFFIX)) {
            String className = fileName.replace(CLASS_FILE_NAME_SUFFIX, "");
            String classNameWithPackageInfo = packagePrefix + PACKAGE_SEPARATOR + className;
            classNames.add(classNameWithPackageInfo);
        }
    }

    private Class<?> putBeanIfAnnotatedAsComponent(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            boolean isAnnotatedAsComponent = clazz.isAnnotationPresent(Component.class);
            if (isAnnotatedAsComponent) {
                validateConcreteClass(clazz);
                putAnnotatedClassInstanceAsBean(className, clazz);
                return clazz;
            }
            return null;
        } catch (ClassNotFoundException e) {
            throw new ComponentScanException(ErrorMessage.CLASS_NOT_FOUND.getMessage(className), e);
        }
    }

    private void validateConcreteClass(Class<?> clazz) {
        if (!isConcreteClass(clazz)) {
            throw new ComponentScanException(ErrorMessage.COMPONENT_ON_NONCONCRETE.getMessage(clazz.getName()));
        }
    }

    private boolean isConcreteClass(Class<?> clazz) {
        boolean isInterface = clazz.isInterface();
        boolean isAbstract = Modifier.isAbstract(clazz.getModifiers());
        return !isInterface && !isAbstract;
    }

    private void putAnnotatedClassInstanceAsBean(String className, Class<?> clazz) {
        Object instance = null;
        while (instance == null && clazz != null && clazz != Object.class) {
            try {
                instance = initiateWithDefaultConstructor(clazz);
            } catch (BeanCreationException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        if (instance != null) {
            beans.put(className, instance);
        }
    }

    private Object initiateWithDefaultConstructor(Class<?> clazz) {
        try {
            Constructor<?> defaultConstructor = clazz.getDeclaredConstructor(null);
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(ErrorMessage.NO_DEFAULT_CONSTRUCTOR.getMessage(clazz.getName()), e);
        } catch (ReflectiveOperationException e) {
            throw new BeanCreationException(ErrorMessage.BEAN_INSTANTIATION_FAILED.getMessage(clazz.getName()), e);
        }
    }

    private void injectDependencies() {
        injectDependenciesByConstructor();
        injectDependenciesByFields();
    }

    private void injectDependenciesByConstructor() {
        for (Class<?> clazz : candidateComponents) {
            Object bean = beans.get(clazz.getName());
            if (bean == null) {
                initiateAndRegisterComponentWithConstructor(clazz);
            }
        }
    }

    private Object initiateAndRegisterComponentWithConstructor(Class<?> clazz) {
        String componentName = clazz.getName();
        try {
            Object bean = injectDependenciesIfAnnotatedAsAutowired(clazz);
            beans.put(componentName, bean);
            return bean;
        } catch (ClassNotFoundException e) {
            throw new ComponentScanException(ErrorMessage.CLASS_NOT_FOUND.getMessage(componentName), e);
        } catch (ReflectiveOperationException e) {
            throw new BeanCreationException(ErrorMessage.BEAN_INSTANTIATION_FAILED.getMessage(componentName), e);
        }
    }

    private Object injectDependenciesIfAnnotatedAsAutowired(Class<?> targetClazz) throws ReflectiveOperationException {
        List<Constructor<?>> annotatedConstructors = findAnnotatedConstructors(targetClazz);
        if (annotatedConstructors.size() > 1) {
            String className = targetClazz.getName();
            throw new DependencyInjectionException(ErrorMessage.TOO_MANY_AUTOWIRED_CONSTRUCTOR.getMessage(className));
        }
        if (annotatedConstructors.isEmpty()) {
            return injectDependenciesWithNoConstructor(targetClazz);
        }
        return injectDependency(targetClazz, annotatedConstructors.getFirst());
    }

    private Object injectDependenciesWithNoConstructor(Class<?> targetClazz) throws ReflectiveOperationException {
        if (isConcreteClass(targetClazz)) {
            validateComponentClass(targetClazz);
            return initiateWithDefaultConstructor(targetClazz);
        }
        return getConcreteClassInstance(targetClazz);
    }

    private void validateComponentClass(Class<?> candidateClass) {
        if (candidateClass.isAnnotationPresent(Component.class)) {
            return;
        }
        throw new DependencyInjectionException(ErrorMessage.BEAN_NOT_FOUND.getMessage(candidateClass));
    }

    private Object getConcreteClassInstance(Class<?> targetClazz) throws ReflectiveOperationException {
        Class<?> concreteClass = findConcreteClass(targetClazz);
        Object instance = beans.get(concreteClass.getName());
        if (instance != null) {
            return instance;
        }
        return injectDependenciesIfAnnotatedAsAutowired(concreteClass);
    }

    private Class<?> findConcreteClass(Class<?> targetClazz) {
        for (Class<?> clazz : candidateComponents) {
            if (targetClazz.isAssignableFrom(clazz)) {
                return clazz;
            }
        }
        throw new BeanCreationException(ErrorMessage.DEPENDENCY_BEAN_NOT_FOUND.getMessage(targetClazz.getName()));
    }

    private Object findBeanByType(Class<?> type, String typeName) {
        List<Object> candidateBeans = new ArrayList<>();
        for (Object bean : beans.values()) {
            if (type.isInstance(bean)) {
                candidateBeans.add(bean);
            }
        }
        if (candidateBeans.isEmpty()) {
            throw new BeanCreationException(ErrorMessage.DEPENDENCY_BEAN_NOT_FOUND.getMessage(typeName));
        }
        if (candidateBeans.size() > 1) {
            throw new DependencyInjectionException(ErrorMessage.TOO_MANY_DEPENDENCY_BEANS.getMessage(typeName));
        }
        return candidateBeans.getFirst();
    }

    private List<Constructor<?>> findAnnotatedConstructors(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        List<Constructor<?>> annotatedConstructors = new ArrayList<Constructor<?>>();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                annotatedConstructors.add(constructor);
            }
        }
        return annotatedConstructors;
    }

    private Object injectDependency(Class<?> target, Constructor<?> constructor) throws ReflectiveOperationException {
        List<Class<?>> parameterTypes = Arrays.stream(constructor.getParameterTypes()).toList();
        if (parameterTypes.isEmpty()) {
            return initiateWithDefaultConstructor(target);
        }
        List<Object> parameters = getParameterInstances(target, parameterTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(parameters.toArray());
    }

    private List<Object> getParameterInstances(Class<?> target, List<Class<?>> parameterTypes) {
        List<Object> parameterInstances = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            if (isConcreteClass(parameterType)) {
                validateComponentClass(parameterType);
            }
            validateCircularDependency(target, parameterType);
            Object parameterInstance = getParameterInstance(parameterType);
            parameterInstances.add(parameterInstance);
        }
        return parameterInstances;
    }

    private void validateCircularDependency(Class<?> target, Class<?> parameterType) {
        Set<Class<?>> checkedClasses = new HashSet<>();
        checkCircularDependency(target, parameterType, checkedClasses);
    }

    private void checkCircularDependency(Class<?> target, Class<?> parameterType, Set<Class<?>> checkedClasses) {
        boolean isChecked = !checkedClasses.add(parameterType);
        if (isChecked) {
            return;
        }
        for (Constructor<?> constructor : parameterType.getDeclaredConstructors()) {
            if (isCircularDependency(target, constructor, checkedClasses)) {
                String clazzName = target.getName();
                throw new DependencyInjectionException(ErrorMessage.CIRCULAR_DEPENDENCY.getMessage(clazzName));
            }
        }
    }

    private boolean isCircularDependency(Class<?> target, Constructor<?> constructor, Set<Class<?>> checkedClasses) {
        for (Class<?> clazz : constructor.getParameterTypes()) {
            if (!isConcreteClass(clazz)) {
                clazz = findConcreteClass(clazz);
            }
            if (clazz.equals(target)) {
                return true;
            }
            checkCircularDependency(target, clazz, checkedClasses);
        }
        return false;
    }

    private Object getParameterInstance(Class<?> parameterType) {
        String typeName = parameterType.getName();
        Object parameterInstance = beans.get(typeName);
        while (parameterInstance == null) {
            parameterInstance = initiateAndRegisterComponentWithConstructor(parameterType);
        }
        return parameterInstance;
    }

    private void injectDependenciesByFields() {
        for (Class<?> clazz : candidateComponents) {
            Object bean = getBean(clazz.getName());
            while (clazz != null && clazz != Object.class) {
                Field[] fields = clazz.getDeclaredFields();
                injectDependenciesIfAnnotatedAsAutowired(bean, fields);
                clazz = clazz.getSuperclass();
            }
        }
    }

    private void injectDependenciesIfAnnotatedAsAutowired(Object bean, Field[] fields) {
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                injectDependency(bean, field);
            }
        }
    }

    private void injectDependency(Object bean, Field field) {
        field.setAccessible(true);
        try {
            field.set(bean, findDependencyBean(field));
        } catch (IllegalAccessException e) {
            String beanClassName = bean.getClass().getName();
            throw new DependencyInjectionException(
                    ErrorMessage.DEPENDENCY_INJECTION_FAILED.getMessage(beanClassName, field.getName()), e);
        }
    }

    private Object findDependencyBean(Field field) {
        try {
            return getBean(field.getType().getName());
        } catch (NoSuchBeanException e) {
            return findBeanByType(field.getType(), field.getName());
        }
    }
}
