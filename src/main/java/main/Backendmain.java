package main;

import actors.Backend;
import actors.ClusterListener;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.management.javadsl.AkkaManagement;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static utils.ConfigUtils.getConfig;

public class Backendmain {
    public static void main(String[] args) {

        String port = args[0];
        final Config config = getConfig("backend", port);

        String clusterName = ConfigFactory.load().getString("clustering.cluster.name");

        // Create Actor System using the configuration
        ActorSystem system = ActorSystem.create(clusterName, config);

        // Start management system only on port 8558
        if (port.equals("2550")) {
            startManagementSystem(system);
        }

        // Create ClusterListener
        system.actorOf(Props.create(ClusterListener.class), String.format("listenerOn%s", port));

        // Create Backend instance
        system.actorOf(Props.create(Backend.class), "backend");
    }

    private static void startManagementSystem(ActorSystem system) {
        AkkaManagement.get(system).start();
    }
}