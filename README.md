# trihug-zookeeper-demo

ZooKeeper demo code for TriHUG May 22, 2012

## Demo One, setting a watch

This example demonstrates the watch mechanism in `ZooKeeper`. All of the basic
ZooKeeper calls (`create`, `delete`, `exists`, `getData`, `getChildren`) take a
`Watcher` object. 

The `Watcher` interface defines a single method - `process(WatchedEvent event)`.
Here, we check for the presence of the znode "/demo".

    zk.exists("/demo", watcher);

This returns a status for the znode and sets a callback on the ZooKeeper server.
The next time something about "/demo" changes (it gets new children, is deleted,
etc), the watcher will get notified.

It's important to note that Watchers are one-shot. So if you want continuous
monitoring of a znode, you need to reset the watch every time you handle an
event.

Run this demo with Ant:

    ant clean demo-1

Then in another terminal, connect to the ZooKeeper server and create "/demo"
with some data:

    ./zk.sh -server localhost:2181
    create /demo DEMO!

## Demo Two, creating a sequential znode

Sequential znodes will automatically append a ten digit zero padded number.
E.g., if you create "foo-" in sequential mode you get "foo-0000000000". If you
create "foo-" again, you get "foo-0000000001". Run the demo like:

    ant clean demo-2

Run it again and see that the incrementor remember where it left off. This is
because the server maintains the incrementor forever. This establishes a
contract that once a sequence of the node has been created, it will never be
used again.

## Demo Three, creating an ephemeral znode

Ephemermal znodes will be automatically deleted by the ZooKeeper server when the
client that created those nodes disconnects. 

Connect to ZooKeeper server and list the children of "/demo" (if "/demo" doesn't
exist, create it).

    ./zk.sh -server localhost:2181
    ls /demo

Run the demo - this creates an ephemeral znode then sleeps for 5 seconds

    ant clean demo-3

Back in the ZooKeeper shell, list the children of "/demo" again

    ls /demo

You should see "ephemeral" along with any other children that might exist. After 
a few seconds, list the children again and "ephermal" should be gone.

N.B., ephemeral and sequential are not mutually exclusive. Znodes are 
(ephemeral or persistent) and (sequential or regular).
