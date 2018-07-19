package org.yanhuang.learning.chronicle;

import net.openhft.chronicle.map.ChronicleMap;

import java.io.IOException;
import java.nio.file.Paths;

public class LongLongMapSize {

	public static void main(String[] args) throws IOException {

		ChronicleMap<Md5Key, Long> longEntryMap = ChronicleMap
				.of(Md5Key.class, Long.class)
				.averageKeySize(16)
				.maxBloatFactor(1.0)
				.entries(10000000)
				.createOrRecoverPersistedTo(Paths.get("/temp/cmap-t1.dat").toFile(), false);
		longEntryMap.close();
	}

	public interface Md5Key{
		// total size is 16 per key
		long getHigh();
		long getLow();
	}

}
