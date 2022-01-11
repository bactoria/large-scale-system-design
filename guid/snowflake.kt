// 출처: https://www.fatalerrors.org/a/snowflake-algorithm-and-uuid-generation-strategy.html
// 2진법 계산기: https://ko.calcuworld.com/%EC%88%98%ED%95%99/2%EC%A7%84%EB%B2%95-%EA%B3%84%EC%82%B0%EA%B8%B0/

fun main(args: Array<String>) {
	val worker = IdWorker(1, 1)
	for (i in 0..49) {
		println(worker.nextId())
	}
}

class IdWorker(private val workerId: Long, private val datacenterId: Long) {
	private val myEpoch = System.currentTimeMillis() // 수정 필요. 서비스 최초 시작시간 고정하면 됨.

	private val datacenterIdBits = 5L
	private val workerIdBits = 5L
	private val sequenceBits = 12L

	private var sequence: Long = 0

	private val workerIdShift = sequenceBits
	private val datacenterIdShift = sequenceBits + workerIdBits
	private val timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits

	// Get the initial UUID, 0000000000000000000000000000000000000000000000000111111 11
	private val sequenceMask = -1L xor (-1L shl sequenceBits.toInt())
	private var lastTimestamp = -1L

	@Synchronized
	fun nextId(): Long {
		var timestamp = timeGen()
		//Time callback, throw exception handling
		//Generally speaking, if the time callback time is short, such as configuring within 5ms, you can directly wait for a certain time to catch up with the machine's time.
		//Extensions can also be used to assign values directly
		if (timestamp < lastTimestamp) {
			System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp)
			throw RuntimeException(
				String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds",
					lastTimestamp - timestamp
				)
			)
		}

		// Control of concurrent access
		if (lastTimestamp == timestamp) {
			sequence = sequence + 1 and sequenceMask
			if (sequence == 0L) {
				timestamp = tilNextMillis(lastTimestamp)
			}
		} else {
			sequence = 0
		}
		lastTimestamp = timestamp
		return timestamp - myEpoch shl timestampLeftShift.toInt() or
				(datacenterId shl datacenterIdShift.toInt()) or
				(workerId shl workerIdShift.toInt()) or
				sequence
	}

	/**
	 * ms is now full
	 */
	private fun tilNextMillis(lastTimestamp: Long): Long {
		var timestamp = timeGen()
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen()
		}
		return timestamp
	}

	// Get the current time
	private fun timeGen(): Long {
		return System.currentTimeMillis()
	}
}
