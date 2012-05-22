# trihug-zookeeper-demo

ZooKeeper demo code for TriHUG May 22, 2012

Build the demos with ant:

    ant clean compile

Included is a ant target that will run the ZooKeeper server:

    ant zk -Dzk.port=2181 -Dzk.dir=/tmp/zk

The ZooKeeper directory can be cleaned up with

    ant clean-zk

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

    ant watcher-demo

Then in another terminal, connect to the ZooKeeper server and create "/demo"
with some data:

    ./zk.sh -server localhost:2181
    create /demo DEMO!

In the Ant window you should see a message about "/demo" being created.

## Demo Two, creating a sequential znode

Sequential znodes will automatically append a ten digit zero padded number.
E.g., if you create "foo-" in sequential mode you get "foo-0000000000". If you
create "foo-" again, you get "foo-0000000001". Run the demo like:

    ant seq-demo

Run it again and see that the incrementor remember where it left off. This is
because the server maintains the incrementor forever. This establishes a
contract that once a sequence of the node has been created, it will never be
used again.

## Demo Three, creating an ephemeral znode

Ephemeral znodes will be automatically deleted by the ZooKeeper server when the
client that created those nodes disconnects. 

Connect to ZooKeeper server and list the children of "/demo" (if "/demo" doesn't
exist, create it).

    ./zk.sh -server localhost:2181
    ls /demo

Run the demo - this creates an ephemeral znode then sleeps for 5 seconds

    ant ephem-demo

Back in the ZooKeeper shell, list the children of "/demo" again

    ls /demo

You should see "ephemeral" along with any other children that might exist. After 
a few seconds, list the children again and "ephemeral" should be gone.

N.B., ephemeral and sequential are not mutually exclusive. Znodes are 
(ephemeral or persistent) and (sequential or regular).

## Demo Four, group membership

This demo utilizes sequential ephemeral nodes to register agents as members of a
group. The group is represented by the znode "/demo/group". When an agent comes
online, it creates an ephemeral sequential node at "/demo/group/member-". Each
agent also sets a watch on getChildren of "/demo/group". This allows every agent
to know about every other agent in real time without polling.

The demo will startup, register itself, sleep for 10 seconds, then terminate.

Run the demo

    ant group-demo

In another terminal

    ant group-demo

# License
Copyright 2012 David Arthur

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.