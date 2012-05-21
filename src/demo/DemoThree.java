package demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/*
 * Ephemeral znode demo
 */
public class DemoThree implements Watcher {

  @Override
  public void process(WatchedEvent event) {
    System.err.println("Event: " + event);
  }

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(args[0]);
    String zkConnect = "localhost:" + port;
    System.err.println("Connecting to: " + zkConnect);
    DemoThree demo = new DemoThree();
    ZooKeeper zk = new ZooKeeper(zkConnect, 10000, demo);
    zk.create("/demo/ephemeral", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    System.err.println("Created /demo/ephemeral. Sleeping for 5 seconds");
    Thread.sleep(5000);
    zk.close();
  }
}
