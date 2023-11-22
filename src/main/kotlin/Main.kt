import java.net.ConnectException

const val PORT = 8080
fun main(args: Array<String>) {
	if (args.contains("-s") || args.contains("--server")) {
		Server(PORT).listen()
	} else {
		println("Enter your username: ")
		try {
			Client(readln()).run()
		} catch (e: ConnectException) {
			println("Failed to connect to server!")
		}
	}
}