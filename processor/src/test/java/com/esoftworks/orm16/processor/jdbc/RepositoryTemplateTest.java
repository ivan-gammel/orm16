package com.esoftworks.orm16.processor.jdbc;

import com.esoftworks.orm16.processor.model.*;
import com.esoftworks.orm16.processor.model.jdbc.RepositoryTemplate;
import com.esoftworks.orm16.processor.view.SerializedEntity;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static com.esoftworks.orm16.core.annotations.MappingContext.PERSISTENCE;
import static com.esoftworks.orm16.processor.model.builder.Builder.given;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepositoryTemplateTest {

    private final Configuration config = given(() -> {
        var config = new Configuration(Configuration.VERSION_2_3_21);
        config.setClassForTemplateLoading(this.getClass(), "/templates");
        return config;
    });

    @Test
    public void shouldGenerateSimpleRepository() throws TemplateException, IOException {

        var properties = List.of(
                new Attribute("uuid", "java.util.UUID", true, emptyMap()),
                new Attribute("created", "java.time.Instant", false, emptyMap()),
                new Attribute("subject", "java.lang.String", false, emptyMap()),
                new Attribute("content", "java.lang.String", false, emptyMap())
        );

        var entity = new Entity(null,"com.example.model", "Document", properties, null, Map.of(PERSISTENCE, new EntityTarget("documents", false)));
        var namespace = new Namespace("com.example.model", List.of(entity), emptyMap());
        var model = new Model(List.of(namespace));
        var view = new SerializedEntity(PERSISTENCE, model, namespace, entity);

        RepositoryTemplate template = new RepositoryTemplate(view, config);

        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);

        template.writeTo(writer);
        writer.flush();
        assertTrue(buffer.toString().length() > 0);
    }

}
