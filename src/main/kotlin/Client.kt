import java.io.IOException
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.Scanner
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class Client(username: String, host: String = "127.0.0.1", port: Int = 8080) {
	private val socket = Socket(host, port)
	private var connected = true

	init {
		if (!this.socket.isConnected || this.socket.isClosed) {
			throw IOException("Connection to server failed!")
		}
		send(username)
	}

	private val reader = Scanner(socket.getInputStream(), StandardCharsets.UTF_8.name())

	/**
	 * Starts the client and listens for input
	 */
	fun run() {
		println("--- Connected to server ---")
		thread { receive() }

		try {
			println("Enter a message to send to the server or 'EXIT' to disconnect: ")
			while (connected) {
				when (val input = readln()) {
					"EXIT" -> disconnect()
					else -> send(input)
				}
			}
		} catch (e: Exception) {
			println("An error occurred, while being connected to the server:")
			println(e.message ?: "No error message provided")
		} finally {
			// Try to send the disconnect, but don't care if there are errors
			send(ID_DISCONNECT, true)
			connected = false
			socket.close()
		}
	}

	/**
	 * Sends a message to the server
	 * @param message The message to send
	 * @throws IOException If the connection is closed
	 */
	private fun send(message: String, ignoreErrors: Boolean = false) {
		try {
			if (!connected) {
				throw IOException("Connection is closed")
			}
			sendMessageToSocket(socket, message)
		} catch (e: Exception) {
			if (!ignoreErrors) {
				throw e
			}
		}
	}

	/**
	 * Checks for new messages via a blocking call.
	 * If the connection is lost, it will print an error and set connected to false
	 */
	private fun receive() {
		while (connected) {
			try {
				val msg = reader.nextLine()
				println("${getTimestamp()} | $msg")
			} catch (e: NoSuchElementException) {
				// Only print error if it was still connected
				if (connected) {
					println("Server connection lost…")
					connected = false
					exitProcess(-1)
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
		println("--- Disconnected from server ---")
	}
}