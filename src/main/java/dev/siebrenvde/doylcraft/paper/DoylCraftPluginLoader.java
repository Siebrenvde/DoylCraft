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
        classpathBuilder.addLibrary(createResolver(
            "siebrenvde",
            "https://repo.siebrenvde.dev/releases/",
            "dev.siebrenvde:ConfigLib:" + BuildParameters.CONFIGLIB_VERSION
        ));
        classpathBuilder.addLibrary(createResolver(
            "maven-central",
            MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR,
            "com.j2html:j2html:" + BuildParameters.J2HTML_VERSION
        ));
    }

    private MavenLibraryResolver createResolver(String repoId, String repoUrl, String... dependencies) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder(repoId, "default", repoUrl).build());
        for (String dependency : dependencies) {
            resolver.addDependency(new Dependency(new DefaultArtifact(dependency), null));
        }
        return resolver;
    }

}
