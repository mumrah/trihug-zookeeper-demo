package demo;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;

import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.listen.Listenable;
import com.netflix.curator.framework.api.CuratorListener;
import com.netflix.curator.framework.api.CuratorEvent;
import com.netflix.curator.retry.RetryOneTime;
import com.netflix.curator.utils.EnsurePath;

public class CuratorGroupMembershipDemo { 

  private final CuratorFramework curator;
  private final Watcher watcher;

  public CuratorGroupMembershipDemo(String zkConnect) throws Exception {
    watcher = new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        try {
          List<String> children = curator.getChildren().usingWatcher(this).forPath("/demo/group");
          System.err.println("Group members: " + children);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    curator = CuratorFrameworkFactory.newClient(zkConnect, 10000, 2000, new RetryOneTime(2000));
    curator.start();
  
    // Ensure the group node exists
    new EnsurePath("/demo/group").ensure(curator.getZookeeperClient());

    // Start watching for children changes
    List<String> children = curator.getChildren().usingWatcher(watcher).forPath("/demo/group");
    System.err.println("Group members: " + children);

    // Register a new member
    String name = curator.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/demo/group/member-");
  }

  public synchronized void close() throws InterruptedException {
    curator.close();
  }

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(args[0]);
    String zkConnect = "localhost:" + port;
    CuratorGroupMembershipDemo demo = new CuratorGroupMembershipDemo(zkConnect);
    Thread.sleep(10000);
    demo.close();
  }
}
