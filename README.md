trihug-zookeeper-demo
=====================

ZooKeeper demo code for TriHUG May 22, 2012

# Demo One, setting a watch

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
montitoring of a znode, you need to reset the watch everytime you handle an
event.

Run this demo with Ant:

 ant clean demo-1

Then in another terminal, connect to the ZooKeeper server and create "/demo"
with some data:

 create /demo DEMO!


