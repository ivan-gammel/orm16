package com.esoftworks.orm16.processor.model.jdbc;

import com.esoftworks.orm16.core.annotations.SerializationContext;
import com.esoftworks.orm16.processor.model.Model;
import com.esoftworks.orm16.processor.ClassTemplate;
import com.esoftworks.orm16.processor.ContextSpecificGenerator;
import com.esoftworks.orm16.processor.view.SerializedView;
import freemarker.template.Configuration;

import java.util.stream.Stream;

public class JdbcCodeGenerator implements ContextSpecificGenerator {

    @Override
    public Stream<ClassTemplate> enumerateTemplates(Model model,
                                                    Configuration config) {

        Stream<ClassTemplate> repositories = new SerializedView(SerializationContext.PERSISTENCE, model)
                .serializedEntities().stream()
                .map(entity -> new RepositoryTemplate(entity, config));

//        Stream<ClassTemplate> generatedEntities = new SerializedView(SerializationContext.PERSISTENCE, model)
//                .generatedEntities()
//                .map(entity -> new GeneratedEntityTemplate(entity, config));

        return repositories;
    }

}
