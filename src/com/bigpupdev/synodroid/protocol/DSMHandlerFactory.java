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

import org.json.JSONObject;

import android.util.Log;

import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.server.SimpleSynoServer;

import com.bigpupdev.synodroid.data.DSMVersion;
import com.bigpupdev.synodroid.protocol.v22.DSHandlerDSM22Factory;
import com.bigpupdev.synodroid.protocol.v31.DSHandlerDSM31Factory;
import com.bigpupdev.synodroid.protocol.v32.DSHandlerDSM32Factory;
import com.bigpupdev.synodroid.protocol.v40.DSHandlerDSM40Factory;

/**
 * This is the protocol abstract factory. The goal of this class is to provide the protocol factory implementation according to the DSM version
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public abstract class DSMHandlerFactory {
	protected String INITDATA_URI = "/webman/initdata.cgi";

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
	public final static DSMHandlerFactory getFactory(DSMVersion versionP, SimpleSynoServer serverP, boolean debug, boolean autoDetect) {
		DSMHandlerFactory result = null;
		// Depending on DSM version
		switch (versionP) {
		case VERSION2_2:
			return new DSHandlerDSM22Factory(serverP, debug, autoDetect);
		case VERSION2_3:
			return new DSHandlerDSM22Factory(serverP, debug, autoDetect);
		case VERSION3_0:
			return new DSHandlerDSM22Factory(serverP, debug, autoDetect);
		case VERSION3_1:
			return new DSHandlerDSM31Factory(serverP, debug, autoDetect);
		case VERSION3_2:
			return new DSHandlerDSM32Factory(serverP, debug, autoDetect);
		case VERSION4_0:
			return new DSHandlerDSM40Factory(serverP, debug, autoDetect);
		}
		return result;
	}
	
	
	protected DSMVersion getVersionFromServer(SimpleSynoServer serverP, boolean autoDetect, boolean debug) throws Exception{
		int version = 0;
		// If we are logged on
		if (serverP.isConnected() && autoDetect) {
			if (debug) Log.d(Synodroid.DS_TAG, "Starting server auto-detection...");
			// Execute
			JSONObject json = null;
			synchronized (serverP) {
				json = serverP.sendJSONRequest(INITDATA_URI, "", "GET");
			}
			if (json != null){
				version = Integer.parseInt(json.getJSONObject("Session").getString("version"));
				if (debug) Log.d(Synodroid.DS_TAG, "Found version: "+version);
				if (version < 1553){
					// DSM 2.2
					return DSMVersion.VERSION2_2;
				}
				else if (version < 1869){
					// DSM 3.1
					return DSMVersion.VERSION3_1;
				}
				else if (version < 2166){
					// DSM 3.2
					return DSMVersion.VERSION3_2;
				}
				else if (version >= 2166){
					// DSM 4.0
					return DSMVersion.VERSION4_0;
				}
			}
		}
		if (debug) Log.d(Synodroid.DS_TAG, "Skipping server auto-detection, will use previous DSM version.");
		return serverP.getDsmVersion();
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
