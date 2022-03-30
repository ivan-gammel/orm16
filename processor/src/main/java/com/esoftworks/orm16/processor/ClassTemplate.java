package com.esoftworks.orm16.processor;

import freemarker.template.TemplateException;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.io.PrintWriter;

public interface ClassTemplate {

    String name();

    Element element();

    void writeTo(PrintWriter writer) throws TemplateException, IOException;

}
