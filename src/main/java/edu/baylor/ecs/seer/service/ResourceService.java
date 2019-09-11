package edu.baylor.ecs.seer.service;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.ClassFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ResourceService {

    // Loader in charge of loading JAR files and ClassFiles
    private final ResourceLoader resourceLoader;

    public ResourceService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<String> getResourcePaths(String folderPath) {
        String directory = new File(folderPath).getAbsolutePath();
        Path start = Paths.get(directory);
        int maxDepth = 15;
        List<String> fileNames = new ArrayList<>();
        try {
            Stream<Path> stream = Files.find(start, maxDepth,
                    (path, attr) ->
                            String.valueOf(path).toLowerCase().endsWith(".jar") ||
                                    String.valueOf(path).toLowerCase().endsWith(".war"));
            fileNames = stream
                    .sorted()
                    .map(String::valueOf)
                    .filter((path) -> {
                        return (String.valueOf(path).toLowerCase().endsWith(".jar") ||
                                String.valueOf(path).toLowerCase().endsWith(".war")) &&
                                !String.valueOf(path).toLowerCase().contains("/.mvn/") &&
                                !String.valueOf(path).toLowerCase().startsWith("/usr/lib/jvm/") &&
                                !String.valueOf(path).toLowerCase().contains("/target/dependency/") &&
                                !String.valueOf(path).toLowerCase().contains("/gradle") &&
                                !String.valueOf(path).toLowerCase().contains("\\.mvn\\") &&
                                !String.valueOf(path).toLowerCase().contains("\\target\\dependency") &&
                                !String.valueOf(path).toLowerCase().contains("\\gradle");
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    Set<Properties> getProperties(String jarPath, String organizationPath) {
        Resource resource = getResource(jarPath);
        Set<Properties> properties = new HashSet<>();
        String uriString = getUriStringFromResource(resource);
        URI u = getUri(uriString);
        Path path = Paths.get(u);

        try (JarFile jar = new JarFile(path.toFile())) {
            List<JarEntry> entries = Collections.list(jar.entries());
            for (JarEntry je : entries) {
                if (isPropertiesFile(je)) {
                    if (je.getName().contains("application")) {
                        Properties prop = getPropertiesFileFromJar(jar, je);
                        if (prop != null) {
                            properties.add(prop);
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public List<CtClass> getCtClasses(String file, String organizationPath) {
        ClassPool cp = ClassPool.getDefault();
        List<CtClass> ctClasses = new ArrayList<>();
        // 1. Get resource
        Resource resource = getResource(file);
        // 2. Get class files
        Set<ClassFile> classFiles = getClassFileSet(resource, organizationPath);

        // Class file to ct class
        for (ClassFile classFile : classFiles) {

            CtClass clazz = null;
            try {
                clazz = cp.makeClass(classFile);
                ctClasses.add(clazz);
            } catch (Exception e) {
                /* LOG */
                System.out.println("Failed to make class:" + e.toString());
                break;
            }
        }
        //return ct classes
        return ctClasses;
    }

    private Resource getResource(String file) {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        if (isWindows) {
            return resourceLoader.getResource("file:/" + file);
        } else {
            return resourceLoader.getResource("file:" + file);
        }
    }

    private Set<ClassFile> getClassFileSet(Resource resource, String organizationPath) {

        /*
         * ToDo: Check organization path on modules layer
         */

        Set<ClassFile> classFiles = new HashSet<>();
        // 2.1
        String uriString = getUriStringFromResource(resource);
        // 2.2
        URI u = getUri(uriString);
        Path path = Paths.get(u);
        try (JarFile jar = new JarFile(path.toFile())) {
            List<JarEntry> entries = Collections.list(jar.entries());
            for (JarEntry je : entries
            ) {
                //2.3
                if (isClassFile(je)) {
                    if (je.getName().contains(organizationPath)) {
                        //2.4
                        ClassFile classFile = getClassFileFromJar(jar, je);
                        if (classFile != null) {
                            classFiles.add(classFile);
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classFiles;
    }

    private String getUriStringFromResource(Resource resource) {
        try {
            return resource.getURI().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private URI getUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isClassFile(JarEntry entry) {
        return entry.getName().endsWith(".class");
    }

    private boolean isPropertiesFile(JarEntry entry) {
        return entry.getName().endsWith(".properties") || entry.getName().endsWith(".yml");
    }

    private ClassFile getClassFileFromJar(JarFile jar, JarEntry entry) {
        /*
         * ToDo: Do not process jars for libraries, just code!
         */
        try (InputStream in = jar.getInputStream(entry)) {
            try (DataInputStream data = new DataInputStream(in)) {
                return new ClassFile(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Properties getPropertiesFileFromJar(JarFile jar, JarEntry entry) {
        Properties prop = null;
        try (InputStream in = jar.getInputStream(entry)) {
            try (DataInputStream data = new DataInputStream(in)) {
                prop = new Properties();
                prop.load(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            prop = null;
        }
        return prop;
    }

}
