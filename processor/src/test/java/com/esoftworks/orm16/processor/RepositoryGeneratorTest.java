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

}
