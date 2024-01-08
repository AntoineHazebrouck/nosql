package jez;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

public class Application
{

	public static void main(String[] args) throws Exception
	{
		Instant start = Instant.now();

		URL url = new URL("https://jsonplaceholder.typicode.com/users/5");
		// + Integer.parseInt(args[0]));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("CODE: " + responseCode); // A priori 200

		String responseContent = new String(con.getInputStream()
				.readAllBytes());

		System.out.println(responseContent);
		System.out.println(Duration.between(start, Instant.now())
				.toMillis());
		con.disconnect();
	}
}
