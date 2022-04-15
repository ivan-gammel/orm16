package com.esoftworks.orm16.processor.model.jdbc;

import com.esoftworks.orm16.core.annotations.MappingContext;
import com.esoftworks.orm16.processor.model.Model;
import com.esoftworks.orm16.processor.ClassTemplate;
import com.esoftworks.orm16.processor.ContextSpecificGenerator;
import com.esoftworks.orm16.processor.view.SerializedView;
import freemarker.template.Configuration;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.stream.Stream;

public class JdbcCodeGenerator implements ContextSpecificGenerator {

    @Override
    public Stream<ClassTemplate> enumerateTemplates(Model model,
                                                    Configuration config, ProcessingEnvironment processingEnv) {

        Stream<ClassTemplate> repositories = new SerializedView(MappingContext.PERSISTENCE, model, processingEnv)
                .serializedEntities().stream()
                .map(entity -> new RepositoryTemplate(entity, config));

//        Stream<ClassTemplate> generatedEntities = new SerializedView(SerializationContext.PERSISTENCE, model)
//                .generatedEntities()
//                .map(entity -> new GeneratedEntityTemplate(entity, config));

        return repositories;
    }

}
