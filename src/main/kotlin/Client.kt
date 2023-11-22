import java.io.IOException
import java.net.ConnectException
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.thread

class Client(username: String, host: String = "127.0.0.1", port: Int = 8080) {
	private val socket = Socket(host, port)
	private var connected = true

	init {
		if (!this.socket.isConnected || this.socket.isClosed) {
			throw IOException("Connection to server failed!")
		}
		println("Connected to $host on port $port")
		send(username)
	}

	private val reader = Scanner(socket.getInputStream(), StandardCharsets.UTF_8.name())

	/**
	 * Starts the client and listens for input
	 */
	fun run() {
		thread { receive() }

		while (connected) {
			println("Enter a message to send:")
			when (val input = readln()) {
				"EXIT" -> disconnect()
				else -> send(input)
			}
		}
	}

	/**
	 * Sends a message to the server
	 * @param message The message to send
	 * @throws IOException If the connection is closed
	 */
	private fun send(message: String) {
		if (!connected) {
			throw IOException("Connection is closed")
		}
		sendMessageToSocket(socket, message)
	}

	/**
	 * Checks for new messages via a blocking call.
	 * If the connection is lost, it will print an error and set connected to false
	 */
	private fun receive() {
		while (connected) {
			try {
				println(reader.nextLine())
			} catch (e: NoSuchElementException) {
				// Only print error if it was still connected
				if (connected) {
					println("Server connection lost…")
					connected = false
				}
			}
		}
	}

	/**
	 * Disconnects from server and ends anything that uses connected as a loop condition
	 */
	private fun disconnect() {
		send(ID_DISCONNECT)
		connected = false
		socket.close()
		println("Disconnected from server")
	}
}