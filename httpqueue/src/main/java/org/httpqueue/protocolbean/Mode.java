package org.httpqueue.protocolbean;

/**
 * Created by andilyliao on 16-3-31.
 */
public class Mode {
    //消息方式 0：1对1 1：1对多 2：订阅
    public final static int DIRECT=0;
    public final static int FANOUT=1;
    public final static int TOPIC=2;
    //是否持久化  0：不持久 1：持久
    public final static int WITHOUTDISK=0;
    public final static int WITHDISK=1;
    //事务 0：无事务 1：有事务
    public final static int NOTRANSACTION=0;
    public final static int TRANSACTION=1;
    //发送方任务类型 0：创建队列 1：发送消息
    public final static int CREATEQUEUE=0;
    public final static int PUBLISH=1;
    //接收方任务类型 0：创建订阅队列 1：接收消息
    public final static int CREATETOPIC=0;
    public final static int CONSUME=1;
}
