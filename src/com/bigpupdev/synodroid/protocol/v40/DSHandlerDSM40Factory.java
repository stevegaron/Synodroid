/**
 * Copyright 2010 Steve Garon
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
package com.bigpupdev.synodroid.protocol.v40;

import com.bigpupdev.synodroid.Synodroid;
import com.bigpupdev.synodroid.server.SimpleSynoServer;
import org.json.JSONObject;

import com.bigpupdev.synodroid.protocol.DSHandler;
import com.bigpupdev.synodroid.protocol.DSMException;
import com.bigpupdev.synodroid.protocol.DSMHandlerFactory;
import com.bigpupdev.synodroid.protocol.QueryBuilder;

import android.util.Log;

/**
 * The factory implementation for DSM v4.0
 * 
 * @author Steve Garon (steve.garon at gmail dot com)
 */
public class DSHandlerDSM40Factory extends DSMHandlerFactory {

	/* Login's constants */
	private static final String LOGIN_PASSWORD_KEY = "passwd";
	private static final String LOGIN_USERNAME_KEY = "username";
	private static final String LOGIN_URI = "/webman/login.cgi";
	private static final String LOGIN_RESULT_KEY = "result";
	private static final String LOGIN_ERROR_REASON = "reason";
	private static final String LOGIN_RESULT_SUCCESS = "success";

	// The Synology's server
	private SimpleSynoServer server;
	// Download station handler
	private DSHandler dsHandler;
	private boolean DEBUG;
	private boolean autoDetect;

	/**
	 * Constructor for the DSM 4.0 handler
	 * 
	 * @param serverP
	 *            The synology server
	 */
	public DSHandlerDSM40Factory(SimpleSynoServer serverP, boolean debug, boolean autoDetectP) {
		server = serverP;
		dsHandler = new DSHandlerDSM40(serverP, debug);
		DEBUG = debug;
		autoDetect = autoDetectP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.protocol.DSMHandlerFactory#connect(com.bigpupdev .synodroid.common.SimpleSynoServer)
	 */
	@Override
	public boolean connect() throws Exception {
		String result = null;
		String reason = null;
		String pass = server.getPassword();
		QueryBuilder builder = new QueryBuilder().add(LOGIN_USERNAME_KEY, server.getUser()).add(LOGIN_PASSWORD_KEY, pass);
		JSONObject respJSO = server.sendJSONRequest(LOGIN_URI, builder.toString(), "POST", false, 0);
		if (DEBUG) Log.d(Synodroid.DS_TAG, "JSON response is:" + respJSO);
		result = respJSO.getString(LOGIN_RESULT_KEY);
		// If no success or not login success
		if (result == null || !result.equals(LOGIN_RESULT_SUCCESS)) {
			reason = respJSO.getString(LOGIN_ERROR_REASON);
			throw new DSMException(reason);
		}
		else{
			server.setConnected(true);
			try{
				server.setDsmVersion(this.getVersionFromServer(server, autoDetect, DEBUG), false);
			}
			catch (Exception e){
				if (DEBUG) Log.e(Synodroid.DS_TAG, "Error while trying to guess DSM version.", e);
			}
			result = server.getDSMHandlerFactory().getDSHandler().getSharedDirectory();
			if (result.equals("")){
				return false;
			}
			else{
				return true;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bigpupdev.synodroid.common.protocol.DSMHandlerFactory#getDSHandler()
	 */
	@Override
	public DSHandler getDSHandler() {
		return dsHandler;
	}

}
