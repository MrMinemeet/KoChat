import java.io.IOException
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.thread

fun main() {
	val client = Client("alex")
	client.run()
}


class Client(private val username: String, host: String = "127.0.0.1", port: Int = 8080) {
	private val socket = Socket(host, port)
	private var connected = true

	init {
		if(!this.socket.isConnected || this.socket.isClosed) {
			throw IOException("Connection to server failed!")
		}
		println("Connected to $host on port $port")
		send(username)
	}

	private val reader = Scanner(socket.getInputStream(), StandardCharsets.UTF_8.name())

	fun run() {
		thread { receive() }

		while(true) {
			println("Enter a message to send: ")
			send(readln())
		}
	}

	fun send(message: String) {
		if (socket.isClosed) {
			throw IOException("Connection closed")
		}
		socket.getOutputStream().write((message + '\n').toByteArray(StandardCharsets.UTF_8))
	}

	/**
	 * Checks for new messages in a polling-style
	 */
	private fun receive() {
		while (connected) {
			println(reader.nextLine())
		}
	}

	/**
	 * Disconnects from server and ends anything that uses connected as a loop condition
	 */
	fun disconnect() {
		connected = false
		socket.close()
		println("Disconnected from server")
	}
}