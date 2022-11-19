import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

fun main(args: Array<String>) {
    val parser = ArgParser("websocket-server")
    val port by parser.option(ArgType.Int, shortName = "p", description = "port").default(8887)
    parser.parse(args)
    embeddedServer(CIO, port = port) {
        install(WebSockets) {
            timeoutMillis = 2000
        }
        routing {
            webSocket {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    println("receive: $receivedText")
                    send("local server $receivedText")
                    send(frame)
                }
            }
        }
    }.start(wait = true)
}