package org.yanhuang.learning.pilosa;

import com.pilosa.client.*;
import com.pilosa.client.orm.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProfileWriteExample {
	public static void main(String[] args) {
		indexTest3ByKey();
	}

	private static void indexTest3ByKey() {
//		PilosaClient client = PilosaClient.withCluster(Cluster.withHost(
//				URI.address("http://192.168.152.103:10101"),
//				URI.address("http://192.168.152.104:10101"),
//				URI.address("http://192.168.155.107:10101")));
//				PilosaClient client = PilosaClient.defaultClient();
		PilosaClient client = PilosaClient.withCluster(Cluster.withHost(
				URI.address("http://192.168.0.105:10101")));
		Schema schema = client.readSchema();
		Index indexTest3 = schema.index("test3");
		Field fieldDaat = indexTest3.field("daat");
		client.query(fieldDaat.set("600001","be9dc9de9a2c81e93d48658cc1ab2936"));
		client.query(fieldDaat.set("600001","H6LBZP1Ozr~"));
		client.query(fieldDaat.set("600002","be9dc9de9a2c81e93d48658cc1ab2936"));
		client.query(fieldDaat.set("600003","H6LBZP1Ozr~"));
		QueryResponse queryResponse = //client.query(fieldDaat.row("600001"));
				client.query(indexTest3.union(fieldDaat.row("600001"),
				fieldDaat.row("600002"), fieldDaat.row("600003")));
		Optional.ofNullable(queryResponse).map(q->q.getResult()).ifPresent(result-> {
			System.out.println("union:");
			System.out.println(result.getRow().getKeys());
		});
		client.query(fieldDaat.clear("600002", "be9dc9de9a2c81e93d48658cc1ab2936"));
		queryResponse = client.query(indexTest3.intersect(fieldDaat.row("600001"),fieldDaat.row("600002")));
		Optional.ofNullable(queryResponse).map(q->q.getResult()).ifPresent(result-> {
			System.out.println("intersect:");
			System.out.println(result.getRow().getKeys());
		});
		try {
			client.close();
		} catch (Exception e) {
			LoggerFactory.getLogger(ProfileWriteExample.class).error("",e);
		}
	}

	private static void indexTest2(String[] args) {
		// Create the default client
		PilosaClient client = PilosaClient.withCluster(Cluster.withHost(
						URI.address("http://192.168.152.103:10101"),
						URI.address("http://192.168.152.104:10101"),
						URI.address("http://192.168.155.107:10101")));

// Retrieve the schema
		Schema schema = client.readSchema();

// Create an Index object
		Index indexTest1 = schema.index("test2");

// Create a Field object
		Field fieldDaat = indexTest1.field("daat");

// make sure the index and field exists on the server
		client.syncSchema(schema);

// Send a SetBit query. PilosaException is thrown if execution of the query fails.
		client.query(fieldDaat.set(5, 42));
//		client.query(fieldDaat.set(5, 43));

// Send a Row query. PilosaException is thrown if execution of the query fails.
		QueryResponse response = client.query(fieldDaat.row(5));

// Get the result
		QueryResult result = response.getResult();

// Act on the result
		if (result != null) {
			List<Long> columns = result.getRow().getColumns();
			System.out.println("Got columns: " + columns);
		}

// You can batch queries to improve throughput
		response = client.query(
				indexTest1.batchQuery(
						fieldDaat.row(5),
						fieldDaat.row(10)
				)
		);
		for (Object r : response.getResults()) {
			// Act on the result
		}
		try {
			client.close();
		} catch (Exception e) {
			LoggerFactory.getLogger(ProfileWriteExample.class).error("",e);
		}
	}
}
