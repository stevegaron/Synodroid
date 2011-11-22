/**
 * Copyright 2010 Eric Taix Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.bigpupdev.synodroid.data;

/**
 * This enumeration enumates the different version managed by this application.
 * 
 * @author Eric Taix (eric.taix at gmail dot com)
 */
public enum DSMVersion {
	VERSION2_2("DSM 2.2"), VERSION2_3("DSM 2.3"), VERSION3_0("DSM 3.0"), VERSION3_1("DSM 3.1"), VERSION3_2("DSM 3.2");

	// The title of this version
	private final String title;

	/**
	 * Constructor which set the title to be displayed in the UI
	 * 
	 * @param titleP
	 */
	private DSMVersion(String titleP) {
		title = titleP;
	}

	/**
	 * Return the title of this value
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return title;
	}

	/**
	 * Return the instance according to the title value
	 * 
	 * @param valueP
	 * @return
	 */
	public static DSMVersion titleOf(String titleP) {
		DSMVersion[] versions = DSMVersion.values();
		for (DSMVersion dsmVersion : versions) {
			if (dsmVersion.getTitle().equalsIgnoreCase(titleP)) {
				return dsmVersion;
			}
		}
		return null;
	}

	/**
	 * Return an array of values
	 * 
	 * @return
	 */
	public static String[] getValues() {
		DSMVersion[] versions = DSMVersion.values();
		String[] values = new String[versions.length];
		for (int iLoop = 0; iLoop < versions.length; iLoop++) {
			values[iLoop] = versions[iLoop].getTitle();
		}
		return values;
	}

}
