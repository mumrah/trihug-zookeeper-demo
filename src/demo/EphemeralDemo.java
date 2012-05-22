package demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/*
 * Ephemeral znode demo
 */
public class EphemeralDemo implements Watcher {

  @Override
  public void process(WatchedEvent event) {
    System.err.println("Event: " + event);
  }

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(args[0]);
    String zkConnect = "localhost:" + port;
    System.err.println("Connecting to: " + zkConnect);
    EphemeralDemo demo = new EphemeralDemo();
    ZooKeeper zk = new ZooKeeper(zkConnect, 10000, demo);
    zk.create("/demo/ephemeral", "I am an ephemeral node".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    System.err.println("Created /demo/ephemeral. Sleeping for 10 seconds");
    Thread.sleep(10000);
    zk.close();
  }
}
