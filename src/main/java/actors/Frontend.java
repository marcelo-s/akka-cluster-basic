package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;

import java.util.ArrayList;
import java.util.List;

import static messages.AppMessages.*;

public class Frontend extends AbstractActor {

    private int jobCounter = 0;
    private List<ActorRef> backends = new ArrayList<>();
    private Cluster cluster = Cluster.get(getContext().system());


    // Subscribe to cluster events
    @Override
    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp.class);
    }

    // Unsubscribe when finished
    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JobMessage.class, job -> hasNoBackends(), this::onJobReceivedNoBackend)
                .match(JobMessage.class, this::onJobReceived)
                .matchEquals(BACKEND_REGISTRATION, this::onBackendRegistrationReceived)
                .match(Terminated.class, this::onBackendTerminationReceived)
                .build();
    }

    private Boolean hasNoBackends() {
        return backends.size() == 0;
    }

    private void onJobReceivedNoBackend(JobMessage job) {
        sender().tell(new FailedMessage("Service unavailable, try again later", job), sender());
    }

    private void onJobReceived(JobMessage job) {
        jobCounter++;
        ActorRef backend = backends.get(jobCounter % backends.size());
        JobMessage jobWithInfo = new JobMessage(job.getPayload());
        backend.forward(jobWithInfo, getContext());
    }

    private void onBackendRegistrationReceived(String str) {
        getContext().watch(sender());
        backends.add(sender());
    }

    private void onBackendTerminationReceived(Terminated terminated) {
        backends.remove(terminated.getActor());
    }
}
