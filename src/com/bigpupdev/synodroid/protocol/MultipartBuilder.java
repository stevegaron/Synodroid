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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import com.bigpupdev.synodroid.Synodroid;

import android.util.Log;

/**
 * A multipart builder to help building a multipart request
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class MultipartBuilder {

	private String lineEnd = "\r\n";
	private String twoHyphens = "--";
	private String boundary;
	// The different parts
	private ArrayList<Part> parts = new ArrayList<Part>();
	private boolean DEBUG;

	/**
	 * Constructor
	 * 
	 * @param boundaryP
	 *            The boundary to use for each part
	 * @param outputP
	 *            The output
	 */
	public MultipartBuilder(String boundaryP, boolean debug) {
		boundary = boundaryP;
		DEBUG = debug;
	}

	/**
	 * Add a part
	 * 
	 * @param partP
	 */
	public MultipartBuilder addPart(Part partP) {
		parts.add(partP);
		return this;
	}

	/**
	 * Get the data from the multipart
	 */
	public void writeData(OutputStream osP) {

		DataOutputStream dos = new DataOutputStream(osP);
		try {
			// For each part
			for (Part part : parts) {
				// Start of the part
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				// The part's name
				dos.writeBytes("Content-Disposition: form-data; name=\"" + part.getName() + "\"");
				// Extras
				Properties extras = part.getExtras();
				Enumeration<Object> enu = extras.keys();
				while (enu.hasMoreElements()) {
					String key = (String) enu.nextElement();
					String value = extras.getProperty(key);
					dos.writeBytes("; " + key + "=\"" + value + "\"");
				}
				// Next line
				dos.writeBytes(lineEnd);
				// The content-type (by default it is plain US-ASCII text)
				if (part.getContentType() != null && part.getContentType().length() > 0) {
					dos.writeBytes("Content-Type: " + part.getContentType() + lineEnd);
				}
				// A blank line
				dos.writeBytes(lineEnd);
				// The content
				byte[] content = part.getContent();
				dos.write(content);
				// Next line
				dos.writeBytes(lineEnd);
			}
			// End of the multipart
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Flush to write datas
			dos.flush();
			dos.close();
		} catch (IOException e) {
			if (DEBUG) Log.e(Synodroid.DS_TAG, "Error while write multipart", e);
		}
	}

	/**
	 * @return the boundary
	 */
	public String getBoundary() {
		return boundary;
	}

}
