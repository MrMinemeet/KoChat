import java.io.IOException
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.thread

fun main() {
	val host = "127.0.0.1"
	val client = Client(host)
	client.run()
}


class Client(host: String, port: Int = 8080) {
	private val socket = Socket(host, port)
	private var connected = true

	init {
		if(!this.socket.isConnected || this.socket.isClosed) {
			throw IOException("Connection to server failed!")
		} else {
			println("Connected to $host on port $port")
		}
	}

	private val reader = Scanner(socket.getInputStream(), StandardCharsets.UTF_8.name())
	private val writer = socket.getOutputStream()

	fun run() {
		thread { receive() }

		while(true) {
			Thread.sleep(200)
		}
	}

	fun send(message: String) {
		if (socket.isClosed) {
			throw IOException("Connection closed")
		}

		val outputStream = socket.getOutputStream()
		outputStream.writer().write(message + "\n")
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