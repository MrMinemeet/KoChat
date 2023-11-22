import java.net.Socket
import java.nio.charset.StandardCharsets

fun sendMessageToSocket(client: Socket, message: String) {
	val writer = client.getOutputStream()
	// Explicit encoding into UTF-8
	writer.write((message + '\n').toByteArray(StandardCharsets.UTF_8))
}


const val ID_DISCONNECT = "#DISCONNECT"