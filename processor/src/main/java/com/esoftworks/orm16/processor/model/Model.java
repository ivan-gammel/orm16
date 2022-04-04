package com.esoftworks.orm16.processor.model;

import com.esoftworks.orm16.core.annotations.MappingContext;

import java.util.List;
import java.util.Optional;

public record Model(List<Namespace> namespaces) {

    public Model {
        if (namespaces == null) throw new NullPointerException("namespaces");
    }

    public boolean supports(MappingContext ctx) {
        return namespaces.stream().anyMatch(ns -> ns.supports(ctx));
    }

    public Optional<Namespace> find(String name) {
        return namespaces.stream().filter(ns -> ns.name().equals(name)).findFirst();
    }

}
