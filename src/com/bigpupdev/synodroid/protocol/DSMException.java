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

/**
 * Base exception of SynoDroid
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
@SuppressWarnings("serial")
public class DSMException extends Exception {

	// The JSON's reason of the exception. Can be null if there's a root cause exception
	private String jsonReason;
	// The root cause exception (more technical errors)
	private Exception rootException;
	
	private int id;

	public boolean isIDException = false;
	/**
	 * Contructor for a JSON error
	 * 
	 * @param msgP
	 */
	public DSMException(String jsonReasonP) {
		super();
		jsonReason = jsonReasonP;
	}

	/**
	 * Contructor for a String ID error
	 * 
	 * @param msgP
	 */
	public DSMException(int idP) {
		super();
		id = idP;
		isIDException = true;
	}

	/**
	 * Contructor for a root exception
	 * 
	 * @param msgP
	 */
	public DSMException(Exception rootCauseP) {
		super();
		rootException = rootCauseP;
	}

	/**
	 * @return the jsonReason
	 */
	public String getJsonReason() {
		return jsonReason;
	}

	/**
	 * @return the rootException
	 */
	public Exception getRootException() {
		return rootException;
	}
	
	/**
	 * @return the stringException
	 */
	public int getExceptionID() {
		return id;
	}
}
