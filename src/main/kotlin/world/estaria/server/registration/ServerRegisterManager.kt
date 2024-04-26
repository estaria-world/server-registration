package world.estaria.server.registration

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import java.net.InetSocketAddress
import java.util.logging.Logger

/**
 * @author Niklas Nieberler
 */

class ServerRegisterManager(
    private val proxyServer: ProxyServer,
    private val logger: Logger
) {

    fun registerServer(serverInfo: ServerInfo) {
        this.proxyServer.registerServer(serverInfo)
        this.logger.info("Register new server ${serverInfo.name} on ${serverInfo.address}")
    }

    fun registerServer(name: String, host: String, port: Int = 25565) {
        val socketAddress = InetSocketAddress.createUnresolved(host, port)
        val serverInfo = ServerInfo(name, socketAddress)
        registerServer(serverInfo)
    }

    fun unregisterServer(name: String) {
        this.proxyServer.getServer(name).ifPresent {
            this.proxyServer.unregisterServer(it.serverInfo)
            this.logger.info("Unregister server $name")
        }
    }

}