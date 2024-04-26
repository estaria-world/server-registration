package world.estaria.server.registration.config

import com.velocitypowered.api.proxy.server.ServerInfo
import kotlinx.serialization.Serializable
import java.net.InetSocketAddress

/**
 * @author Niklas Nieberler
 */

@Serializable
class ServerRegisterInfo(
    private val name: String,
    private val host: String,
    private val port: Int?
) {

    fun toServerInfo(): ServerInfo {
        val socketAddress = InetSocketAddress.createUnresolved(this.host, this.port ?: 25565)
        return ServerInfo(this.name, socketAddress)
    }

}