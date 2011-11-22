/**
 * 
 */
package com.bigpupdev.synodroid.protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import android.net.Uri;

/**
 * An utility class which generates a stream from an Uri
 * 
 * @author Eric Taix (eric.taix at gmail.com)
 */
public class StreamFactory {

	/**
	 * Return the stream according to the Uri
	 * 
	 * @param uriP
	 * @return
	 * @throws Exception
	 */
	public static byte[] getStream(Uri uriP) throws Exception {
		try {
			return StreamFactory.decompressStream(uriP);
		} catch (Exception ex) {
			String path = uriP.getPath();
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);

			int maxBufferSize = 1 * 1024 * 1024;
			int bytesAvailable = fis.available();
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];
			fis.read(buffer, 0, bufferSize);
			fis.close();
			return buffer;
		}
	}

	private static byte[] decompressStream(Uri uriP) throws Exception {
		String path = uriP.getPath();
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		GZIPInputStream gis = new GZIPInputStream(fis);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = gis.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();

		gis.close();
		fis.close();
		return buffer.toByteArray();

	}

}
