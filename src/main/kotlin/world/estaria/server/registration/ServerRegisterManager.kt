package world.estaria.server.registration

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import io.fabric8.kubernetes.api.model.Pod
import java.net.InetSocketAddress
import java.util.logging.Logger

/**
 * @author Niklas Nieberler
 */

class ServerRegisterManager(
    private val proxyServer: ProxyServer,
    private val logger: Logger
) {

    fun registerServer(pod: Pod) {
        val podIp = pod.status.podIP
        val podName = pod.metadata.name
        registerServer(podName, podIp)
    }

    fun isServerRegistered(pod: Pod): Boolean {
        val name = pod.metadata.name
        return this.proxyServer.getServer(name).isPresent
    }

    fun isServerRegistered(serverInfo: ServerInfo): Boolean {
        return this.proxyServer.getServer(serverInfo.name).isPresent
    }

    fun getServers(): List<ServerInfo> {
        return this.proxyServer.allServers.map { it.serverInfo }
    }

    fun registerServer(serverInfo: ServerInfo) {
        this.proxyServer.registerServer(serverInfo)
        this.logger.info("Register new server ${serverInfo.name} on ${serverInfo.address}")
    }

    private fun registerServer(name: String, host: String, port: Int = 25565) {
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