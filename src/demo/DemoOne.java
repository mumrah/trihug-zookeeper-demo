package demo;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/*
 * Znode watcher demo
 */
public class DemoOne implements Watcher {

  private volatile boolean _sawDemo = false;
  private static final String NODE = "/demo";

  public boolean sawDemo() {
    return _sawDemo;
  }

  @Override
  public void process(WatchedEvent event) {
    System.err.println("Event: " + event);

    // Look for a NodeCreate event on /demo
    if(event.getType() == Watcher.Event.EventType.NodeCreated) {
      if(event.getPath().equals(NODE)) {
        _sawDemo = true;
      } 
    }
  }

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(args[0]);
    String zkConnect = "localhost:" + port;

    DemoOne demo = new DemoOne();
    ZooKeeper zk = new ZooKeeper(zkConnect, 10000, demo);
    Stat demoPath= zk.exists(NODE, true);
    if(demoPath != null) {
      System.err.println("Demo exists, deleting it first");
      zk.delete(NODE, -1);
      zk.exists(NODE, true); // Reset the watch
    }
    while(true) {
      if(demo.sawDemo()) {
        System.err.println("Demo was created!");
        break;
      }
      Thread.sleep(100);
    }
    zk.close();
  }
}   
