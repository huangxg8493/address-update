package com.address.utils;

import java.security.SecureRandom;

/**
 * 雪花算法 ID 生成器
 * 结构: 1符号位 + 41时间戳 + 5 workerId + 5 datacenterId + 12序列号
 */
public class SnowflakeIdGenerator {

    private static final long START_EPOCH = 1609459200000L; // 2021-01-01 毫秒时间戳

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS); // 31
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 31

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS); // 4095

    private static final SnowflakeIdGenerator INSTANCE = new SnowflakeIdGenerator(1, 1);

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private SecureRandom random = new SecureRandom();

    private SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId 必须在 0-31 之间");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId 必须在 0-31 之间");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public static SnowflakeIdGenerator getInstance() {
        return INSTANCE;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            // 时钟回拨，等待追上
            timestamp = waitUntilNextMillis(lastTimestamp);
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitUntilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - START_EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    public String nextIdAsString() {
        return String.valueOf(nextId());
    }

    public synchronized Long generate8DigitId() {
        // 使用2^20=1048576最接近1000000，用&运算取后20位再%1000000限制范围
        long timestamp = (System.currentTimeMillis() & 0xFFFFF) % 1000000;
        int randomPart = random.nextInt(100);
        return Long.parseLong(String.format("%06d%02d", timestamp, randomPart));
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
}
