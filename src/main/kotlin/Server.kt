import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.thread

fun main() {
	val port = 8080
	Server(port).listen()
}

class Server(private val port: Int) {
	object SServer {
		val messagesToSend = mutableListOf<Message>()
		val clientList = mutableListOf<Socket>()
	}

	private val server = ServerSocket(port)

	fun listen() {
		println("Server listening on port $port")
		thread { distributeMessages() }
		while (true) {
			val socket = server.accept()
			thread {
				SServer.clientList.add(socket)
				ClientHandler(socket).run()
			}
		}
	}

	private fun distributeMessages() {
		while (true) {
			if (SServer.messagesToSend.isNotEmpty()) {
				val msg = SServer.messagesToSend.removeAt(0)
				println("Forwarding message from ${msg.sender}")
				SServer.clientList.forEach { socket ->
					sendToClient(socket, msg.toString())
				}
			}
			Thread.sleep(1000)
		}
	}

	class ClientHandler(private val client: Socket) {
		private val reader = Scanner(client.getInputStream())


		fun run() {
			// First message of a client is the name
			val name = reader.nextLine()
			println("User '$name' with address '${client.inetAddress.hostAddress}' connected")
			while (true) {
				try {
					val message = reader.nextLine()
					println("Message from $name received: $message")
					Message(name, message).let { SServer.messagesToSend.add(it) }
				} catch (e: NoSuchElementException) {
					println("Client disconnected: ${client.inetAddress.hostAddress}")
					break
				}
			}
		}
	}
}

fun sendToClient(client: Socket, message: String) {
	val writer = client.getOutputStream()
	// Explicit encoding into UTF-8
	writer.write((message + '\n').toByteArray(StandardCharsets.UTF_8))
}

data class Message(val sender: String, val content: String) {
	override fun toString(): String {
		return "$sender: $content"
	}
}