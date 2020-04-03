/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE content distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this content to You under the Apache License, Version 2.0
 * (the "License"); you may not use this content except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bio4j.ng.commons.types;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the service to expose. Notice we must extend {@link Remote} which
 * is required when using RMI.
 *
 * @version 
 */
public interface HelloService extends Remote {

    String hello(String name) throws RemoteException;

}
