package jez;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TP55RestAvecCache
{
	public static void main(String[] args) throws Exception
	{
		int userId = 6;
		Instant start = Instant.now();


		JedisPool pool = new JedisPool("localhost", 6379);

		try (Jedis jedis = pool.getResource())
		{
			if (!jedis.exists("user:" + userId))
			{
				String userContent = fetchUser(userId);

				jedis.set("user:" + userId, userContent);

				System.out.println("set user:" + userId + " to " + userContent);
			} else
			{
				System.out.println("user " + userId + " is already in redis");
			}
			pool.close();
		}


		System.out.println(Duration.between(start, Instant.now())
				.toMillis());

	}

	static String fetchUser(int id) throws Exception
	{
		URL url = new URL("https://jsonplaceholder.typicode.com/users/5");
		// + Integer.parseInt(args[0]));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("CODE: " + responseCode); // A priori 200

		String responseContent = new String(con.getInputStream()
				.readAllBytes());

		return responseContent;
	}
}
