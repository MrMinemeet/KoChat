import java.net.ConnectException

const val PORT = 8080
fun main(args: Array<String>) {
	if (args.contains("-s") || args.contains("--server")) {
		Server(PORT).listen()
	} else {
		println("Enter a address to connect to: ")
		val address = readln()

		println("Enter your username: ")
		try {
			Client(readln(), host=address).run()
		} catch (e: ConnectException) {
			println("Failed to connect to server!")
		}
	}
}