
const val PORT = 8080
fun main(args: Array<String>) {
	if (args.contains("-s") || args.contains("--server")) {
		Server(PORT).listen()
	} else {
		println("Enter your username: ")
		Client(readln()).run()
	}
}