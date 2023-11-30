import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

class Server(private val port: Int) {
	private val server = ServerSocket(port)

	// List of all connected clients
	private val connectedClients = mutableListOf<ClientData>()

	/**
	 * Listens for incoming client requests.
	 * Also catches any exceptions and ends the server in a soft manner
	 */
	fun listen() {
		try {
			listenerLoop()
		} catch (e: Exception) {
			Logger.log("!An error occurred while running the KoChat Server!")
			Logger.log(e.message ?: "No error message provided.")
		} finally {
			server.close()
		}
	}

	/**
	 * Listens for incoming client connections
	 * Starts up a new thread for each client
	 */
	private fun listenerLoop() {
		Logger.log("Server listening on port $port")
		while (true) {
			val client = server.accept()
			thread {
				ClientHandler(client).run()
			}
		}
	}

	/**
	 * Sends a message to all clients except the sender
	 * @param message The message to send
	 */
	private fun distributeMessages(message: Message) {
		connectedClients
			.filterNot { it == message.sender }
			.forEach { (_, socket) ->
				sendMessageToSocket(socket, message.toString())
			}
	}

	private inner class ClientHandler(private val client: Socket) {
		private val reader = Scanner(client.getInputStream())

		/**
		 * Handles incoming messages from a client
		 */
		fun run() {
			// First message of a client is the name
			val senderName: String = try {
				reader.nextLine()
			} catch (e: NoSuchElementException) {
				Logger.log("No name received from '${client.inetAddress.hostAddress}:${client.port}'")
				client.close()
				return
			}
			val sender = ClientData(senderName, client)
			connectedClients.add(sender)
			Logger.log("User '${sender}' connected")
			while (true) {
				try {
					val message = reader.nextLine()
					if (message == ID_DISCONNECT) {
						disconnect(sender)
						break
					}

					Logger.log("Message from $sender: $message")
					val msg = Message(sender, message)
					distributeMessages(msg)
				} catch (e: NoSuchElementException) {
					Logger.log("User '$sender' lost connection")
					break
				}
			}
		}

		/**
		 * Disconnects a client
		 */
		private fun disconnect(sender: ClientData) {
			client.close()
			connectedClients.remove(sender)
			Logger.log("User '$sender' disconnected")
		}
	}
}

/**
 * Message from a client with content.
 * @param sender The client that sent the message
 * @param content The content of the message
 */
data class Message(val sender: ClientData, val content: String) {
	override fun toString(): String {
		return "${sender.name}: $content"
	}
}

/**
 * Data class for a client
 * @param name The name of the client
 * @param client The socket of the client
 */
data class ClientData(val name: String, val client: Socket) {
	override fun toString(): String {
		return "$name <${client.inetAddress.hostAddress}:${client.port}>"
	}

	override fun equals(other: Any?): Boolean {
		if (other is ClientData) {
			return this.name == other.name && this.client == other.client
		}
		return false
	}

	override fun hashCode(): Int {
		return name.hashCode() + client.hashCode()
	}
}