package com.example.ilia.final_exercise.data.api;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Created by ilia on 23.06.15.
 * @author ilia
 */
public class ApiResponse {
	private static final String CHARSET = "UTF-8";
	private static final int BUFFER_SIZE = 1024;
	private InputStream mInputSream;
	private int status;

	public ApiResponse() {
		this(0, null);
	}

	public ApiResponse(int status, InputStream inputStream) {
		this.status = status;
		this.mInputSream = inputStream;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public String getAsText() {
		final char[] buffer = new char[BUFFER_SIZE];
		final StringBuilder out = new StringBuilder();
		try (Reader in = new InputStreamReader(mInputSream, CHARSET)) {
			for (; ; ) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return out.toString();

	}

	public InputStreamReader getInputStreamReader() {
		if (mInputSream == null) {
			return null;
		}
		return new InputStreamReader(mInputSream);
	}

	public int getStatus() {
		return status;
	}
}
