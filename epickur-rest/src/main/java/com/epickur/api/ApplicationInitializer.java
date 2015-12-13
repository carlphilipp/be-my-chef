package com.epickur.api;

import com.epickur.api.config.ApplicationConfig;
import com.epickur.api.filter.HeaderResponseFilter;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class ApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] {
				ApplicationConfig.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		final DelegatingFilterProxy keyRequestFilter = new DelegatingFilterProxy("keyRequestFilter");
		final DelegatingFilterProxy logRequestFilter = new DelegatingFilterProxy("logRequestFilter");
		return new Filter[] { new HeaderResponseFilter(), logRequestFilter, keyRequestFilter };
	}

	@Override
	public void onStartup(final ServletContext servletContext) throws ServletException {
		servletContext.setInitParameter("quartz:shutdown-on-unload", "true");
		servletContext.setInitParameter("quartz:wait-on-shutdown", "true");
		servletContext.setInitParameter("quartz:start-scheduler-on-load", "true");
		servletContext.addListener(QuartzInitializerListener.class);
		super.onStartup(servletContext);
	}

	@Override
	protected void customizeRegistration(final ServletRegistration.Dynamic registration) {
		registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
	}
}
