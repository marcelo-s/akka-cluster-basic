package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import main.Main;

import java.util.ArrayList;
import java.util.List;

import static messages.AppMessages.*;

public class Frontend extends AbstractActor {

    int jobCounter = 0;
    List<ActorRef> backends = new ArrayList<>();
    Cluster cluster = Cluster.get(getContext().system());


    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp.class);
    }

    //re-subscribe when restart
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
//        String address = String.format("akka.tcp://%s@127.0.0.1:%s", Main.CLUSTER_SYSTEM_NAME, 2550);
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
