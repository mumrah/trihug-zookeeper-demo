package demo;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

/*
 * Znode watcher demo
 */
public class WatcherDemo implements Watcher {

  private static final String NODE = "/demo";
  private volatile boolean _sawDemo = false;

  public boolean sawDemo() {
    return _sawDemo;
  }

  @Override
  public void process(WatchedEvent event) {
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

    WatcherDemo demo = new WatcherDemo();
    ZooKeeper zk = new ZooKeeper(zkConnect, 10000, demo);

    // If /demo exists, delete it first
    if(zk.exists(NODE, true) != null) {
      System.err.println("Demo exists, deleting it first");
      zk.delete(NODE, -1);
      zk.exists(NODE, true); // Reset the watch
    }

    // Poll until the watcher gets called
    while(true) {
      if(demo.sawDemo()) {
        System.err.print("Demo was created!");
        break;
      }
      Thread.sleep(100);
    }
    System.err.println(" With data: " + new String(zk.getData(NODE, false, null)));
    zk.close();
  }
}   
