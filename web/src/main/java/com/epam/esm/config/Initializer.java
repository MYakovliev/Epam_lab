package com.epam.esm.config;


import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class Initializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{TestConfig.class, SpringConfigLogic.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
