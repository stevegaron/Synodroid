package com.bigpupdev.synodroid.data;

public class Folder {
	// The public name of the directory
	public String id;
	// The public name of the directory
	public String name;
	// The public name of the directory
	public String path;
	// Is this directory is the current global shared directory ?
	public boolean isCurrent = false;

	/**
	 * A constructor which intialize all parameters
	 * 
	 * @param idP
	 * @param nameP
	 */
	public Folder(String idP, String nameP, String pathP) {
		name = nameP;
		id = idP;
		path = pathP;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Folder other = (Folder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

