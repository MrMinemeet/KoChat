import java.net.Socket
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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

const val ID_DISCONNECT = "#DISCONNECT"

object Logger {
	fun log(message: String) {
		val timestamp = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneOffset.UTC)
			.format(Instant.now())
		println("$timestamp -> $message")
	}
}