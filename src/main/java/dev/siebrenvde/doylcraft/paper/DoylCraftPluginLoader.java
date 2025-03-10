package dev.siebrenvde.doylcraft.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings({"UnstableApiUsage", "unused"})
@NullMarked
public class DoylCraftPluginLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder(
            "siebrenvde",
            "default",
            "https://files.siebrenvde.dev/repository/releases/"
        ).build());
        resolver.addDependency(new Dependency(
            new DefaultArtifact("dev.siebrenvde:ConfigLib:0.2.0"),
            null
        ));

        classpathBuilder.addLibrary(resolver);
    }

}
