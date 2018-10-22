package org.yanhuang.learning.springboot21.rocksdb;

import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class DbUnderJdk11Task {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Scheduled(initialDelay = 3000, fixedDelay = 60000)
	public void dbOperation() {
		logger.info("db operation task running");

		final Options options = new Options();
		final Filter bloomFilter = new BloomFilter(10);
		final Statistics stats = new Statistics();
		final RateLimiter rateLimiter = new RateLimiter(150000000, 10000, 10);

		final BlockBasedTableConfig table_options = new BlockBasedTableConfig();
		table_options.setBlockCacheSize(24 * SizeUnit.MB).setBlockSize(8 * SizeUnit.KB)
				.setFilter(bloomFilter).setCacheIndexAndFilterBlocks(true)
				.setPinL0FilterAndIndexBlocksInCache(true);
		// db options
		options.setCreateIfMissing(true).setStatistics(stats).setTableFormatConfig(table_options)
				.setWriteBufferSize(8 * SizeUnit.MB).setMaxWriteBufferNumber(5)
				.setLevel0FileNumCompactionTrigger(4).setNumLevels(10).setMaxBytesForLevelBase(8 * SizeUnit.MB)
				.setMaxBytesForLevelMultiplier(10).setTargetFileSizeBase(8 * SizeUnit.MB)
				.setMaxBackgroundCompactions(4).setSkipStatsUpdateOnDbOpen(true) // 机械硬盘，优化启动速度
				.setCompressionType(CompressionType.NO_COMPRESSION).setCompactionStyle(CompactionStyle.LEVEL)// 机械硬盘，减小磁盘的读取
				.setLevelCompactionDynamicLevelBytes(true).setNewTableReaderForCompactionInputs(true)
				.setCompactionReadaheadSize(2 * SizeUnit.MB).setOptimizeFiltersForHits(true)
				.setRateLimiter(rateLimiter).setStatsDumpPeriodSec(60).setReportBgIoStats(true)
				.setLogFileTimeToRoll(TimeUnit.DAYS.toSeconds(1));

		try (options; bloomFilter; stats; rateLimiter; RocksDB db = RocksDB.open(options, "/bigdata/test/jdk11");) {
			ByteBuffer bb = ByteBuffer.allocate(8);
			for (int i = 0; i < 1024 * 10; i++) {
				final byte[] keyBytes = UUID.randomUUID().toString().getBytes();
				final long randLong = ThreadLocalRandom.current().nextLong();
				bb.putLong(randLong);
				final byte[] valueBytes = bb.array();
				try {
					db.put(keyBytes, valueBytes);
				} catch (RocksDBException e) {
					logger.error("write kv to db error", e);
				}
				bb.rewind();
			}
		} catch (Exception e) {
			logger.error("create rocksdb error", e);
		}
		logger.info("db operation task finish");
	}

}
