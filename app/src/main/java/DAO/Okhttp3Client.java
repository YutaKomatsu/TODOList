package DAO;

import okhttp3.OkHttpClient;

public class Okhttp3Client {
	private static OkHttpClient client;
	
	static {
		client = new OkHttpClient();
	}
	public static OkHttpClient getOkHttpClient() {
		return client;
	}
}
