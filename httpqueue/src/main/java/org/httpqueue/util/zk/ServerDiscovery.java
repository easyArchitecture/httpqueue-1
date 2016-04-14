package org.httpqueue.util.zk;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by andilyliao on 16-4-14.
 */
public class ServerDiscovery implements Watcher {
    private static Logger log = Logger.getLogger(ServerDiscovery.class);

    private static final int SESSION_TIMEOUT = 10000;
    private static final String CONNECTION_STRING = "zookeeper01:2181,zookeeper02:2181,zookeeper03:2181";
    private static final String ZK_PATH = "/test/";
    private static final String HOSTNAME=UUID.randomUUID().toString();
    private ZooKeeper zk = null;

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public void registServer() throws IOException, InterruptedException, KeeperException {
        this.releaseConnection();
        zk = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, this);
        connectedSemaphore.await();
        this.zk.create(ZK_PATH+HOSTNAME,HOSTNAME .getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
    }

    public void releaseConnection() throws InterruptedException {
        if (this.zk != null) {
            this.zk.close();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        log.debug("Recive message notify：" + watchedEvent.getState());
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
