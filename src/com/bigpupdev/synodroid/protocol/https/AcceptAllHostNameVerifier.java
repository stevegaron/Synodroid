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
package com.bigpupdev.synodroid.protocol.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * This class always return true when it tries to verify a hostname in a ssl session. Why ? Because when using self signed certificate (like Synology do),
 * 
 * @author Eric Taix (eric.taix at gmail dot com)
 */
public class AcceptAllHostNameVerifier implements HostnameVerifier {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String, javax.net.ssl.SSLSession)
	 */
	public boolean verify(String hostNameP, SSLSession sslSessionP) {
		return true;
	}

}
