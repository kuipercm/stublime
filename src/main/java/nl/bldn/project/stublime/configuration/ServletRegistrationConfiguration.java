package nl.bldn.project.stublime.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

@Configuration
public class ServletRegistrationConfiguration {
    static final String REST_SERVLET_NAME = "rest";
    static final String REST_URL_MAPPING = "/rest/*";

    static final String SOAP_SERVLET_NAME = "soap";
    static final String SOAP_URL_MAPPING = "/soap/*";

    @Bean
    public ServletRegistrationBean restRegistrationBean(ApplicationContext context) {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setApplicationContext(context);

        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(servlet, REST_URL_MAPPING);
        servletRegistrationBean.setName(REST_SERVLET_NAME);
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean soapRegistrationBean(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);

        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(servlet, SOAP_URL_MAPPING);
        servletRegistrationBean.setName(SOAP_SERVLET_NAME);
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

}
