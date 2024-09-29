package com.techcourse;

import com.interface21.webmvc.servlet.ModelAndView;
import com.interface21.webmvc.servlet.mvc.asis.Controller;
import com.interface21.webmvc.servlet.mvc.tobe.HandlerMappingRegistry;
import com.interface21.webmvc.servlet.mvc.tobe.handlermapping.AnnotationHandlerMapping;
import com.interface21.webmvc.servlet.view.JspView;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private final HandlerMappingRegistry handlerMappingRegistry;
    private final ManualHandlerMapping manualHandlerMapping;

    public DispatcherServlet() {
        handlerMappingRegistry = new HandlerMappingRegistry();
        manualHandlerMapping = new ManualHandlerMapping();
    }

    @Override
    public void init() {
        handlerMappingRegistry.addHandlerMapping(new ManualHandlerMapping());
        handlerMappingRegistry.addHandlerMapping(new AnnotationHandlerMapping(Application.class));
        manualHandlerMapping.initialize();
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException {
        logRequest(request);
        try {
            final Controller controller = manualHandlerMapping.getHandler(request);
            final String viewName = controller.execute(request, response);
            renderView(viewName, request, response);
        } catch (Throwable e) {
            handleException(e);
        }
    }

    private void logRequest(final HttpServletRequest request) {
        log.debug("Method : {}, Request URI : {}", request.getMethod(), request.getRequestURI());
    }

    private void renderView(final String viewName, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final JspView jspView = new JspView(viewName);
        final ModelAndView modelAndView = new ModelAndView(jspView);
        jspView.render(modelAndView.getModel(), request, response);
    }

    private void handleException(final Throwable e) throws ServletException {
        log.error("Exception : {}", e.getMessage(), e);
        throw new ServletException(e.getMessage(), e);
    }
}
