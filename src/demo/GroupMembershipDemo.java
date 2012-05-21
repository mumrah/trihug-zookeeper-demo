package demo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/*
 * Group membership demo
 */
public class GroupMembershipDemo {

  private final CountDownLatch latch = new CountDownLatch(1);
  private final Watcher connectionWatcher;
  private final Watcher childrenWatcher;
  private ZooKeeper zk;

  public GroupMembershipDemo(String zkConnect) throws IOException, InterruptedException, KeeperException {
    connectionWatcher = new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        System.err.println("Connection Event: " + event);
        if(event.getType() == Watcher.Event.EventType.None &&
            event.getState() == Watcher.Event.KeeperState.SyncConnected) {
          latch.countDown();
        }   
      }
    };
    
    childrenWatcher = new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        System.err.println("Children Event: " + event);
        try {
          List<String> children = zk.getChildren("/demo/group", this);
          System.err.println("Members: " + children);
        } catch (KeeperException e) {
          throw new RuntimeException(e);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(e);
        }
      }
    };

    zk = new ZooKeeper(zkConnect, 10000, connectionWatcher);
    latch.await(10, TimeUnit.SECONDS); // Wait 10 seconds for the connection to sync

    // Ensure the group node exists
    if(zk.exists("/demo/group", false) == null) {
      zk.create("/demo/group", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // Set a watch on the group
    List<String> children = zk.getChildren("/demo/group", childrenWatcher);
    System.err.println("Members: " + children);

    // Register a new member
    String out = zk.create("/demo/group/member-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
  }

  public synchronized void close() throws InterruptedException {
    zk.close();
  }

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(args[0]);
    String zkConnect = "localhost:" + port;
    System.err.println("Connecting to: " + zkConnect);
    GroupMembershipDemo demo = new GroupMembershipDemo(zkConnect);
    Thread.sleep(10000);
    demo.close();
  }
}
