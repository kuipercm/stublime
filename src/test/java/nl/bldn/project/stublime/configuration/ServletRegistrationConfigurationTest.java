package nl.bldn.project.stublime.configuration;

import static nl.bldn.project.stublime.configuration.ServletRegistrationConfiguration.REST_SERVLET_NAME;
import static nl.bldn.project.stublime.configuration.ServletRegistrationConfiguration.REST_URL_MAPPING;
import static nl.bldn.project.stublime.configuration.ServletRegistrationConfiguration.SOAP_SERVLET_NAME;
import static nl.bldn.project.stublime.configuration.ServletRegistrationConfiguration.SOAP_URL_MAPPING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;

public class ServletRegistrationConfigurationTest {
    private final ServletRegistrationConfiguration configuration = new ServletRegistrationConfiguration();
    private final ApplicationContext applicationContext = mock(ApplicationContext.class);

    @Test
    public void rest_servlet_bean_is_registered_under_correct_path_and_with_correct_startup_parameters() {
        ServletRegistrationBean sut = configuration.restRegistrationBean(applicationContext);

        assertThat(sut.getServletName()).isEqualTo(REST_SERVLET_NAME);
        assertThat(sut.getUrlMappings()).containsExactly(REST_URL_MAPPING);
    }

    @Test
    public void soap_servlet_bean_is_registered_under_correct_path_and_with_correct_startup_parameters() {
        ServletRegistrationBean sut = configuration.soapRegistrationBean(applicationContext);

        assertThat(sut.getServletName()).isEqualTo(SOAP_SERVLET_NAME);
        assertThat(sut.getUrlMappings()).containsExactly(SOAP_URL_MAPPING);
    }
}