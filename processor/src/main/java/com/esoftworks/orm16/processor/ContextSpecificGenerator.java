package com.esoftworks.orm16.processor;

import com.esoftworks.orm16.processor.model.Model;
import freemarker.template.Configuration;

import java.util.stream.Stream;

public interface ContextSpecificGenerator {

    Stream<ClassTemplate> enumerateTemplates(Model model, Configuration config);

}
