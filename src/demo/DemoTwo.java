package demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/*
 * Sequential znode demo
 */
public class DemoTwo implements Watcher {

  @Override
  public void process(WatchedEvent event) {
    System.err.println("Event: " + event);
  }

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(args[0]);
    String zkConnect = "localhost:" + port;
    System.err.println("Connecting to: " + zkConnect);
    DemoTwo demo = new DemoTwo();
    ZooKeeper zk = new ZooKeeper(zkConnect, 10000, demo);
    for(int i=0; i<4; i++) {
      zk.create("/demo/seq-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
    }
    zk.close();
  }
}
