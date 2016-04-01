package org.httpqueue.inprocess;

import org.httpqueue.protocolbean.InputHead;

/**
 * Created by andilyliao on 16-3-31.
 */
public interface IInProcessor {
    public void process(InputHead inputHead, String body) throws Exception;
}
