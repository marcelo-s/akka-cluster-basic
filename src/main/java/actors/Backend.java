package actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.SupervisorStrategy;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import messages.AppMessages.JobMessage;
import messages.AppMessages.ResultMessage;

import static messages.AppMessages.BACKEND_REGISTRATION;

public class Backend extends AbstractLoggingActor {

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
    public SupervisorStrategy supervisorStrategy() {
        return super.supervisorStrategy();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JobMessage.class, this::onJobReceived)
                .match(ClusterEvent.MemberUp.class, this::onMemberUp)
                .build();
    }

    private void onJobReceived(JobMessage job) {
        log().info("BACKEND >>>>>> RECEIVED JOB : {} FROM : {}", job.getPayload(), sender().path().toString());
        sender().tell(new ResultMessage(String.format("RESULT : %s FROM BACKEND : %s", job.getPayload().toUpperCase(), self().path())), self());
    }

    private void onMemberUp(ClusterEvent.MemberUp memberUp) {
        Member member = memberUp.member();
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    BACKEND_REGISTRATION, self());
    }
}

