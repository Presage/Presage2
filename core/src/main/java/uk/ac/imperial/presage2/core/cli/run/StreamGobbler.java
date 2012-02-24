/**
 * 	Copyright (C) 2011 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
 *
 * 	This file is part of Presage2.
 *
 *     Presage2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Presage2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser Public License
 *     along with Presage2.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.imperial.presage2.core.cli.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * StreamGobbler: Consumes an {@link InputStream} and redirects it to a given
 * {@link OutputStream} or discards it. This is used to ensure subprocesses
 * properly terminate (see http://kylecartmell.com/?p=9).
 * 
 * @author Sam Macbeth
 * 
 */
public class StreamGobbler extends Thread {

	private InputStream is;
	private OutputStream os;

	public StreamGobbler(InputStream is) {
		// create with null output stream
		this(is, new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		});
	}

	public StreamGobbler(InputStream is, OutputStream os) {
		super();
		this.is = is;
		this.os = os;
	}

	@Override
	public void run() {
		PrintWriter pw = new PrintWriter(os);

		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				pw.write(line + "\n");
			}
			pw.flush();

		} catch (IOException e) {

		}

	}
}
