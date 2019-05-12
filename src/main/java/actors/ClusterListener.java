package actors;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClusterListener extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    Cluster cluster = Cluster.get(getContext().getSystem());

    // subscribe to cluster changes
    @Override
    public void preStart() {
        cluster.subscribe(
                getSelf(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    // re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberWeaklyUp.class, mWeekly ->
                        log.info("********MEMBER WEEKLY: {}", mWeekly.member())
                )
                .match(
                        ClusterEvent.MemberJoined.class,
                        mJoin -> log.info("********* MEMBER JOINED: {}", mJoin.member()))
                .match(
                        ClusterEvent.MemberUp.class,
                        mUp -> log.info("********** MEMBER UP: {}", mUp.member()))
                .match(
                        ClusterEvent.UnreachableMember.class,
                        mUnreachable -> log.info("******* MEMBER UNREACHABLE: {}", mUnreachable.member()))
                .match(
                        ClusterEvent.MemberRemoved.class,
                        mRemoved -> log.info("*************** MEMBER REMOVED: {}", mRemoved.member()))
                .match(
                        ClusterEvent.MemberEvent.class,
                        message -> log.info("************UNKNOWN EVENT**************"))
                .build();
    }
}
