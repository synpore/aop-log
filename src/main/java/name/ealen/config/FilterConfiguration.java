package name.ealen.config;

import name.ealen.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by EalenXie on 2018/9/7 15:56.
 * Http请求拦截器,日志打印请求相关信息
 */
@Configuration
public class FilterConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FilterConfig.class);

    @Bean
    @Order(Integer.MIN_VALUE)
    @Qualifier("filterRegistration")
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(controllerFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    private Filter controllerFilter() {
        return new Filter() {
            @Override
            public void init(FilterConfig filterConfig) {
                log.info("ControllerFilter init Success");
            }

            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                String requestId = request.getHeader("Request-Id");
                if (requestId == null) requestId = request.getRequestedSessionId();
                if (requestId == null) requestId = UUID.randomUUID().toString();
                if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    System.out.println();
                    log.info("Http Request Request-Id : " + requestId);
                    log.info("Http Request Information : {\"URI\":\"" + request.getRequestURL() +
                            "\",\"RequestMethod\":\"" + request.getMethod() +
                            "\",\"ClientIp\":\"" + CommonUtil.getIpAddress(request) +
                            "\",\"Content-Type\":\"" + request.getContentType() +
                            "\",\"UserAgent\":\"" + request.getHeader(HttpHeaders.USER_AGENT) +
                            "\"}");
                }
                chain.doFilter(request, response);
            }

            @Override
            public void destroy() {
                log.info("ControllerFilter destroy");
            }
        };
    }
}
