import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

fun main() {
	Server(PORT).listen()
}

class Server(private val port: Int) {
	private val server = ServerSocket(port)

	object SServer {
		val clientList = mutableListOf<ClientData>()


		fun distributeMessages(msg: Message) {
			clientList
				.filterNot { it == msg.sender }
				.forEach { (_, socket) ->
					sendMessageToSocket(socket, msg.toString())
				}
		}
	}

	fun listen() {
		println("Server listening on port $port")
		while (true) {
			val socket = server.accept()
			thread {
				ClientHandler(socket).run()
			}
		}
	}

	class ClientHandler(private val client: Socket) {
		private val reader = Scanner(client.getInputStream())

		fun run() {
			// First message of a client is the name
			val senderName: String = try {
				reader.nextLine()
			} catch (e: NoSuchElementException) {
				println("No name received from '${client.inetAddress.hostAddress}:${client.port}'")
				client.close()
				return
			}
			val sender = ClientData(senderName, client)
			SServer.clientList.add(sender)
			println("User '${sender}' connected")
			while (true) {
				try {
					val message = reader.nextLine()
					if (message == ID_DISCONNECT) {
						disconnect(sender)
						break
					}

					println("Message from $sender: $message")
					val msg = Message(sender, message)
					SServer.distributeMessages(msg)
				} catch (e: NoSuchElementException) {
					println("User '$sender' lost connection")
					break
				}
			}
		}

		private fun disconnect(sender: ClientData) {
			client.close()
			SServer.clientList.remove(sender)
			println("User '$sender' disconnected")
		}
	}
}

data class Message(val sender: ClientData, val content: String) {

	override fun toString(): String {
		return "${sender.name}: $content"
	}
}

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