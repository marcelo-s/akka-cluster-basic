package main;

import actors.Frontend;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import messages.AppMessages;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.ConfigUtils.getConfig;

public class FrontendMain {

    public static void main(String[] args) {

        String port = args[0];
        final Config config = getConfig("frontend", port);

        String clusterName = ConfigFactory.load().getString("clustering.cluster.name");

        // Create Actor System using the configuration
        ActorSystem system = ActorSystem.create(clusterName, config);

        // Create Frontend instance
        final ActorRef frontend = system.actorOf(
                Props.create(Frontend.class), "frontend");

        // Ticker to simulate jobs coming from user
        final FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
        final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        final ExecutionContext ec = system.dispatcher();
        final AtomicInteger counter = new AtomicInteger();
        system.scheduler().schedule(
                interval,
                interval,
                () -> Patterns.ask(
                        frontend,
                        new AppMessages.JobMessage("hello-" + counter.incrementAndGet()),
                        timeout)
                        .onComplete(result -> {
                            System.out.println(result);
                            return CompletableFuture.completedFuture(result);
                        }, ec)
                , ec);
    }
}
