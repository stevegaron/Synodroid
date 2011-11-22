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

import java.util.Properties;

/**
 * A part of a multipart request
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class Part {

	// The name of this part
	private String name;
	// Extra values
	private Properties extras = new Properties();
	// The data
	private byte[] data;
	// The content-type (don't set 'content-type:' but just application/octet-stream for example
	private String contentType = null;

	/**
	 * Constructor
	 * 
	 * @param nameP
	 * @param extrasP
	 */
	public Part(String nameP) {
		name = nameP;
	}

	public Part addExtra(String nameP, String valueP) {
		extras.put(nameP, valueP);
		return this;
	}

	/**
	 * Set the content
	 * 
	 * @param dataP
	 * @return
	 */
	public Part setContent(byte[] dataP) {
		data = dataP;
		return this;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the extras
	 */
	public Properties getExtras() {
		return extras;
	}

	/**
	 * @return the data
	 */
	public byte[] getContent() {
		return data;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
