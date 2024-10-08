package uk.ipfreely.mvn.plugin.iana;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import uk.ipfreely.mvn.plugin.iana.gen.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Mojo(name="registries", defaultPhase=LifecyclePhase.GENERATE_SOURCES)
public class RegistriesMojo extends AbstractMojo {
    private final Xml xml = new Xml();

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/iana")
    private File jsonDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/iana")
    private File outputDirectory;

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public File getJsonDirectory() {
        return jsonDirectory;
    }

    public void execute() throws MojoExecutionException {
        createOutput(getOutputDirectory());
        createOutput(getJsonDirectory());

        getLog().info("Generating registries");

        try {
            process("iana-ipv4-special-registry.json", IanaIpv4SpecialRegistry.bytes());
            process("iana-ipv6-special-registry.json", IanaIpv6SpecialRegistry.bytes());
            process("multicast-addresses.json", MulticastAddresses.bytes());
            process("ipv6-multicast-addresses.json", Ipv6MulticastAddresses.bytes());
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoExecutionException(e.toString());
        }
    }

    private void createOutput(File out) {
        if (out.isDirectory()) {
            return;
        }
        boolean made = out.mkdirs();
        if (!made) {
            getLog().error("Unable to create directory " + out);
        }
    }

    private void process(String name, byte[] data) throws IOException {
        Map<String, Object> parsed = xml.special(data);

        writeJson(name, parsed);
    }

    private void writeJson(String name, Map<String, Object> map) throws IOException {
        getLog().info("Writing " + name);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(map);
        File output = new File(jsonDirectory, name);
        try (OutputStream out = new FileOutputStream(output)) {
            out.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }
}
