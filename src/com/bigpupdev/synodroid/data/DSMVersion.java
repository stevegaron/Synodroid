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
	VERSION4_3("DSM 4.3", 43), VERSION4_2("DSM 4.2", 42), VERSION4_1("DSM 4.1", 41), VERSION4_0("DSM 4.0", 40), VERSION3_2("DSM 3.2", 32), VERSION3_1("DSM 3.1", 31), VERSION3_0("DSM 3.0", 30), VERSION2_3("DSM 2.3", 23), VERSION2_2("DSM 2.2", 22);

	// The title of this version
	private final String title;
	private final int value;

	/**
	 * Constructor which set the title to be displayed in the UI
	 * 
	 * @param titleP
	 */
	private DSMVersion(String titleP, int valueP) {
		title = titleP;
		value = valueP;
	}

	/**
	 * Return the title of this value
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	* Return the numeral value of this entry
	*
	* @return
	*/
	public int getValue() {
		return value;
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
	 * Return latest version
	 * 
	 * @return
	 */
	public static DSMVersion getLatest() {
		DSMVersion[] versions = DSMVersion.values();
		DSMVersion last = null;
		for (int iLoop = 0; iLoop < versions.length; iLoop++) {
			if (last == null){
				last = versions[iLoop];
			}
			else if (last.getValue() < versions[iLoop].getValue()){
				last = versions[iLoop];
			}
		}
		return last;
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
	
	public boolean smallerThen(DSMVersion v) {
		return (getValue() < v.getValue());
	}
	
	public boolean greaterThen(DSMVersion v) {
		return (getValue() > v.getValue());
	}
}
