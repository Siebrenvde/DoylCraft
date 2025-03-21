package dev.siebrenvde.doylcraft.paper;

import dev.siebrenvde.doylcraft.utils.BuildParameters;
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
            "https://repo.siebrenvde.dev/snapshots/" // TODO: Change back to releases
        ).build());
        resolver.addDependency(new Dependency(
            new DefaultArtifact("dev.siebrenvde:ConfigLib:" + BuildParameters.CONFIGLIB_VERSION),
            null
        ));

        classpathBuilder.addLibrary(resolver);
    }

}
