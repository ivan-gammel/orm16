package com.esoftworks.orm16.processor;

import com.google.testing.compile.Compilation;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forResource;

public class RepositoryGeneratorTest {

    @Test
    public void shouldCompileSimpleRepositoryWithoutErrors() {
        Compilation compilation = javac()
                .withProcessors(new RepositoryGenerator())
                .compile(forResource("com/example/Document.java"));

        assertThat(compilation).succeeded();

        assertThat(compilation)
                     .generatedSourceFile("DocumentRepository")
                     .hasSourceEquivalentTo(forResource("com/example/expected/DocumentRepository.java"));
    }

    @Test
    public void shouldCompileEmbeddedEntityWithoutErrors() {
        Compilation compilation = javac()
                .withProcessors(new RepositoryGenerator())
                .compile(forResource("com/example/PersonalName.java"),
                         forResource("com/example/EmbedExample.java"));

        assertThat(compilation).succeeded();

        assertThat(compilation)
                     .generatedSourceFile("EmbedExampleRepository")
                     .hasSourceEquivalentTo(forResource("com/example/expected/EmbedExampleRepository.java"));
    }

    @Test
    public void shouldReportMissingIdAnnotationAsError() {
        Compilation compilation = javac()
                .withProcessors(new RepositoryGenerator())
                .compile(forResource("com/example/MissingId.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("@Id");
    }

}
