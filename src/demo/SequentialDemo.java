package demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/*
 * Sequential znode demo
 */
public class SequentialDemo {

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(args[0]);
    String zkConnect = "localhost:" + port;
    System.err.println("Connecting to: " + zkConnect);

    ZooKeeper zk = new ZooKeeper(zkConnect, 10000, null);
    System.err.println("Creating four sequential znodes at /demo/seq-");

    for(int i=0; i<4; i++) {
      zk.create("/demo/seq-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
    }
    zk.close();
  }

}
