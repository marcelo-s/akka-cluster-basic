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
        String path = self().path().toString();
        String name = self().path().name();

        log().info(String.format("FROM FRONTEND >>>>>> %s ; JOB: %s", sender().path().toString(), job.getPayload()));
        sender().tell(new ResultMessage(String.format("FROM BACKEND >>>>>> %s ; RESULT : %s", self().path(), job.getPayload().toUpperCase())), self());
    }

    private void onMemberUp(ClusterEvent.MemberUp memberUp) {
        Member member = memberUp.member();
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    BACKEND_REGISTRATION, self());
    }
}

