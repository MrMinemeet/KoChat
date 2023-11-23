import java.net.Socket
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Indicates a proper disconnect from the server */
const val ID_DISCONNECT = "#DISCONNECT"

/**
 * Sends a message to a socket
 * @param client The socket to send the message to
 * @param message The message to send
 */
fun sendMessageToSocket(client: Socket, message: String) {
	val writer = client.getOutputStream()
	// Explicit encoding into UTF-8
	writer.write((message + '\n').toByteArray(StandardCharsets.UTF_8))
}

/**
 * Gets the current timestamp in UTC
 * @return The timestamp as a string
 */
fun getTimestamp(zId: ZoneId = ZoneId.systemDefault()): String {
	return DateTimeFormatter
		.ofPattern("yyyy-MM-dd HH:mm:ss")
		.withZone(zId)
		.format(Instant.now())
}

object Logger {
	/**
	 * Logs a message to the console
	 * @param message The message to log
	 */
	fun log(message: String) {
		println("${getTimestamp()} -> $message")
	}
}