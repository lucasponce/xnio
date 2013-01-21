/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xnio.conduits;

import java.nio.channels.Channel;
import org.xnio.ChannelListeners;
import org.xnio.channels.CloseListenerSettable;
import org.xnio.channels.WriteListenerSettable;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface WriteReadyHandler extends TerminateHandler {
    void writeReady();

    class ChannelListenerHandler<C extends Channel & WriteListenerSettable<C> & CloseListenerSettable<C>> implements WriteReadyHandler {
        private final C channel;

        public ChannelListenerHandler(final C channel) {
            this.channel = channel;
        }

        public void writeReady() {
            ChannelListeners.invokeChannelListener(channel, channel.getWriteListener());
        }

        public void terminated() {
            ChannelListeners.invokeChannelListener(channel, channel.getCloseListener());
        }
    }

    class ReadyTask implements Runnable {

        private final WriteReadyHandler handler;

        public ReadyTask(final WriteReadyHandler handler) {
            this.handler = handler;
        }

        public void run() {
            handler.writeReady();
        }
    }
}