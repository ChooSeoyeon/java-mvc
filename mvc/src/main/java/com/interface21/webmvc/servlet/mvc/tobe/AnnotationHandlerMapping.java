package com.interface21.webmvc.servlet.mvc.tobe;

import com.interface21.web.bind.annotation.RequestMethod;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationHandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private final Object[] basePackage;
    private final Map<HandlerKey, HandlerExecution> handlerExecutions;

    public AnnotationHandlerMapping(final Object... basePackage) {
        this.basePackage = basePackage;
        this.handlerExecutions = new HashMap<>();
    }

    public void initialize() {
        Controllers controllers = new Controllers(basePackage);
        MappingMethods methods = new MappingMethods(controllers);
        for (Method method : methods.getMethods()) {
            List<HandlerKey> keys = createKeys(method, methods);
            HandlerExecution execution = createExecution(method, controllers);
            for (HandlerKey key : keys) {
                addHandler(key, execution);
            }
        }
        log.info("Initialized AnnotationHandlerMapping!");
    }

    private List<HandlerKey> createKeys(Method method, MappingMethods methods) {
        String url = methods.getUrl(method);
        RequestMethod[] requestMethods = methods.getRequestMethods(method);
        return Arrays.stream(requestMethods)
                .map(requestMethod -> new HandlerKey(url, requestMethod))
                .toList();
    }

    private HandlerExecution createExecution(Method method, Controllers controllers) {
        Object controller = controllers.getController(method.getDeclaringClass());
        return new HandlerExecution(controller, method);
    }

    private void addHandlers(String url, RequestMethod[] requestMethods, HandlerExecution execution) {
        for (RequestMethod requestMethod : requestMethods) {
            HandlerKey key = new HandlerKey(url, requestMethod);
            addHandler(key, execution);
        }
    }

    private void addHandler(HandlerKey key, HandlerExecution execution) {
        if (handlerExecutions.containsKey(key)) {
            throw new IllegalArgumentException("이미 등록된 URL과 HTTP 메서드 조합입니다: " + key);
        }
        handlerExecutions.put(key, execution);
    }

    public Object getHandler(final HttpServletRequest request) {
        String url = request.getRequestURI();
        RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod());
        HandlerKey key = new HandlerKey(url, requestMethod);
        return handlerExecutions.get(key);
    }
}
