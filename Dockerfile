FROM gradle
ADD --chown=gradle . /akka/cluster/app
WORKDIR /akka/cluster/app
CMD gradle run --args="backend 2570"
