package com.interface21.webmvc.servlet.mvc.tobe;

import com.interface21.web.bind.annotation.RequestMapping;
import com.interface21.web.bind.annotation.RequestMethod;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MappingMethods {

    private final Map<Method, RequestMapping> methods = new HashMap<>();

    public MappingMethods(Controllers controllers) {
        controllers.getControllerClasses().stream()
                .flatMap(clazz -> Arrays.stream(clazz.getMethods()))
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .forEach(method -> methods.put(method, method.getAnnotation(RequestMapping.class)));
    }

    public RequestMapping get(Method method) {
        return methods.get(method);
    }

    public String getUrl(Method method) {
        return get(method).value();
    }

    public RequestMethod[] getRequestMethods(Method method) {
        return get(method).method();
    }

    public Set<Method> getMethods() {
        return methods.keySet();
    }
}
