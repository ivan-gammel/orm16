package com.esoftworks.orm16.processor.model.jdbc;

import com.esoftworks.orm16.processor.ClassTemplate;
import com.esoftworks.orm16.processor.view.SerializedEntity;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record RepositoryTemplate(SerializedEntity entity,
                                 Configuration config) implements ClassTemplate {

    @Override
    public String name() {
        return entity.name() + "Repository";
    }

    public String packageName() {
        return entity.packageName() + ".jdbc";
    }

    @Override
    public Element element() {
        return entity.element();
    }

    public List<String> imports() {
        return Stream.concat(entity.imports(), Stream.of("com.esoftworks.orm16.core.repository.Repository")).toList();
    }

    public List<String> interfaces() {
        return Stream.of("Repository<" + entity.name() + ", " + entity.key().type().name() + ">").toList();
    }

    @Override
    public void writeTo(PrintWriter writer) throws TemplateException, IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("repository", this);
        model.put("entity", entity);
        Template template = config.getTemplate("Repository.ftl");
        template.process(model, writer);
    }

}
