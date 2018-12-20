package innossh.grpc.quickstart.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import io.grpc.BindableService;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcServerBuilder {

    private static final Logger LOGGER = Logger.getLogger(GrpcServerBuilder.class.getName());

    public static GrpcServerBuilder forPackage(String packageName) throws IOException {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        ImmutableSet<ClassPath.ClassInfo> classes = ClassPath.from(cl).getTopLevelClasses(packageName);
        LOGGER.info(packageName + " is loaded.");
        final List<BindableService> services = new LinkedList<>();
        classes.asList().stream().forEach(c -> {
            Class clazz = c.load();
            Arrays.stream(clazz.getAnnotations()).forEach(annotation -> {
                if (annotation instanceof GrpcService) {
                    try {
                        BindableService service = (BindableService) clazz.newInstance();
                        services.add(service);
                        LOGGER.info(clazz.getName() + " is registered.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.log(Level.WARNING, "Failed to register the service:" + clazz.getName(), e);
                    }
                }
            });
        });
        return new GrpcServerBuilder(services);
    }

    private final List<BindableService> services;

    private GrpcServerBuilder(List<BindableService> services) {
        this.services = services;
    }

    public GrpcServer build() {
        return new GrpcServer(services);
    }

}
