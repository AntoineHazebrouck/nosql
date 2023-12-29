package fr.but3;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Application {

	public static void main(String[] args) {
		String connectionString = "mongodb://localhost:27017";
		MongoClient mongoClient = MongoClients.create(connectionString);
		MongoDatabase database = mongoClient.getDatabase("test");
		MongoCollection<Document> collection = database.getCollection("users");
		long nb = collection.countDocuments();
		System.out.println("Nombre de documents: " + nb);
		FindIterable<Document> documents = collection.find().limit(10);
		for (Document d : documents)
			System.out.println(d.toJson());
		mongoClient.close();

		System.out.println("Hello World!");
	}
}
