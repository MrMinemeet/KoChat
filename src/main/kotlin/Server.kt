import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.thread

fun main () {
	val port = 8080
	Server(port).listen()
}

class Server(private val port: Int) {
	private val server = ServerSocket(port)

	private val clientList = mutableMapOf<Socket, String>()

	fun listen() {
		println("Server listening on port $port")
		while (true) {
			val socket = server.accept()
			thread {ClientHandler(socket).run() }
		}
	}

	class ClientHandler(private val client: Socket) {
		private val reader = Scanner(client.getInputStream())
		private val writer = client.getOutputStream()

		init {
			println("Client connected: ${client.inetAddress.hostAddress}")
		}

		fun run() {
			while (true) {
				write("Hello, world!")
				Thread.sleep(2000)
			}
		}

		private fun write(message: String) {
			println("Sending '$message'…")
			// Explicit encoding into UTF-8
			writer.write((message + '\n').toByteArray(StandardCharsets.UTF_8))
		}
	}
}

