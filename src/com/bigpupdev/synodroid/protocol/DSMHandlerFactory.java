/**
 * Copyright 2010 Eric Taix
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 */
package com.bigpupdev.synodroid.protocol;

import com.bigpupdev.synodroid.server.SynoServer;

import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.protocol.v22.DSHandlerDSM22Factory;
import com.bigpupdev.synodroid.protocol.v31.DSHandlerDSM31Factory;

/**
 * This is the protocol abstract factory. The goal of this class is to provide the protocol factory implementation according to the DSM version
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public abstract class DSMHandlerFactory {

	/**
	 * Private constructor: no need to instanciate
	 */
	protected DSMHandlerFactory() {
	}

	/**
	 * Return the appropriate protocol according to the DSM version
	 * 
	 * @return
	 */
	public final static DSMHandlerFactory getFactory(DSMVersion versionP, SynoServer serverP, boolean debug) {
		DSMHandlerFactory result = null;
		// Depending on DSM version
		switch (versionP) {
		case VERSION2_2:
			return new DSHandlerDSM22Factory(serverP, debug);
		case VERSION2_3:
			return new DSHandlerDSM22Factory(serverP, debug);
		case VERSION3_0:
			return new DSHandlerDSM22Factory(serverP, debug);
		case VERSION3_1:
			return new DSHandlerDSM31Factory(serverP, debug);
		case VERSION3_2:
			return new DSHandlerDSM31Factory(serverP, debug);
		}
		return result;
	}

	/**
	 * Connect to a SynoServer. This method MUST be called prior to any other methods.
	 * 
	 * @param serverP
	 */
	public abstract boolean connect() throws Exception;

	/**
	 * Return the Download station handler
	 * 
	 * @return
	 */
	public abstract DSHandler getDSHandler();
}
