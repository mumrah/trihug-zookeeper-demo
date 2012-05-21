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
public class GroupMembershipDemo implements Watcher {

  private final CountDownLatch latch = new CountDownLatch(1);
  private ZooKeeper zk;

  public GroupMembershipDemo(String zkConnect) throws IOException, InterruptedException, KeeperException {
    zk = new ZooKeeper(zkConnect, 10000, this);
    latch.await(10, TimeUnit.SECONDS);
    // Connection should be good to go now
    // Ensure the group node exists
    if(zk.exists("/demo/group", false) == null) {
      zk.create("/demo/group", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // Register a new member
    String out = zk.create("/demo/group/member-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    System.err.println("Registering as " + out);

    // Set a watch on the group
    List<String> children = zk.getChildren("/demo/group", true);
    System.err.println("Members: " + children);

  }

  public void close() throws InterruptedException {
    zk.close();
  }

  @Override
  public void process(WatchedEvent event) {
    System.err.println("Event: " + event);
    if(event.getType() == Watcher.Event.EventType.None &&
        event.getState() == Watcher.Event.KeeperState.SyncConnected) {
      // Connection event
      latch.countDown();
    }   
    if(event.getPath().equals("/demo/group") && 
        event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
      // Group members changed
      System.err.println("Something about the group changed");
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
  }

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(args[0]);
    String zkConnect = "localhost:" + port;
    System.err.println("Connecting to: " + zkConnect);
    final GroupMembershipDemo demo = new GroupMembershipDemo(zkConnect);

    // Make sure we close cleanly
    Runtime.getRuntime().addShutdownHook(new Thread(){
      @Override
      public void run() {
        try {
          demo.close();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(e);
        }
      }
    });

    while(true) {
      Thread.sleep(100);
    }
  }
}
