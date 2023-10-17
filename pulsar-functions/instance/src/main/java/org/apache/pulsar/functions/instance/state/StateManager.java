/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.functions.instance.state;

import org.apache.pulsar.functions.api.StateStore;

/**
 * A state manager that manages multiple state stores.
 */
public interface StateManager extends AutoCloseable {

    /**
     * Register the state store.
     *
     * @param store the state store to register.
     */
    void registerStore(StateStore store);

    /**
     * Get the state store with the given name.
     *
     * @param tenant the state store tenant.
     * @param namespace the state store namespace.
     * @param name the state store name.
     * @return the state store with the given name.
     */
    StateStore getStore(String tenant, String namespace, String name);

    void close();

}
