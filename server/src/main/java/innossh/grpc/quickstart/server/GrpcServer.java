package innossh.grpc.quickstart.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class GrpcServer {

    private static final Logger LOGGER = Logger.getLogger(GrpcServer.class.getName());

    private Server server;

    public GrpcServer(List<BindableService> services) {
        int port = 50051;
        ServerBuilder serverBuilder = ServerBuilder.forPort(port);
        services.stream().forEach(service -> serverBuilder.addService(service));
        server = serverBuilder.build();
    }

    public void start() throws IOException, InterruptedException {
        server.start();
        LOGGER.info("Server started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.severe("Shutting down gRPC server since JVM is shutting down");
            GrpcServer.this.stop();
            LOGGER.severe("Server shut down");
        }));
        server.awaitTermination();
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

}
