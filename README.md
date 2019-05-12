# Akka Cluster (locally)

Basic cluster with frontend and backend

## Terms

Cluster : Group of nodes

Node: An actor system running a JVM. It is possible to have multiple actor systems in one JVM.

Frontend: Node/System creator of tasks

Backend: Node/System worker of tasks coming from Frontend 

### Prerequisites

Java 1.8+
Gradle 5.2+

### Installing/Running

* Clone project
* Quick start:
```
gradle run
```
To start one by one:
* To start a node:
```
CMD gradle run --args="backend 2550"
```

As the configuration is, at the following nodes need to be up:
```
backend 2550 # seed node 1 
backend 2560 # seed node 2
```

And at least one frontend to start sending jobs:

```
frontend 2590  
```

### Description

Project starts with class "Main" on the main package. 
If no arguments are provided the autoStart method is executed.
There are other two classes as well on the main package:

* Backendmain
* Frontendmain

Each one initializes the respective systems

main.Main: it creates two instances of the backend, with specified ports.

The constant name: CLUSTER_SYSTEM_NAME has to be the same across all the systems created in the cluster.
This allows for all the created actor systems to belong to the same cluster otherwise it will create individual
clusters.

main.Backendmain: Node/System in charge of receiving tasks from frontend and producing a result. It is listening to "MemberUp"
events, so in case a new "frontend" member joins, it will tell it that it is available to process tasks.

main.Frontendmain: Node/System in charge of assigning tasks to backend. It has a list of backends that can process
some work. 

Backend: Actor class for the backend, that listens and carry out tasks from frontend.

Frontend: Actor class that registers workers(backends) and forward the tasks to an available backend.

AppMessages: All the messages for the application. In general there are 3 types of messages: 

* JobMessage,  which carries the job to be done. 
* ResultMessage, message that carries the result of the computation
* FailedMessage, message that carries information about the failing computation 

### Application.conf file

Basic configuration file. Provider is set to cluster. The netty tcp is set to localhost for testing and the port is
"0", this will assign a port automatically. However since the ports are being overridden on the properties for the
nodes, at the creation of the actor system, see Frontendmain.java and main.Backendmain.java, the overridden port will take precedence. 

The backends are designed as seed nodes. The first one on the list should be the first to start to create the cluster. 
After this, the other nodes will be joining using the first node to join the cluster.

### Cluster management (HTTP)

The cluster management is set to use the HTTP API, that can be found on:
 [Cluster Http Management](https://developer.lightbend.com/docs/akka-management/current/cluster-http-management.html)

To use it for queries and actions, is better to use Postman: [Postman](https://www.getpostman.com/)

The address and port are set in the "application.conf" file under the "management" section