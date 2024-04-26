package world.estaria.server.registration.plugin

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import world.estaria.server.registration.PodResourceEventHandler
import world.estaria.server.registration.ServerRegisterManager
import world.estaria.server.registration.config.ConfigMapHandler
import java.util.logging.Logger

/**
 * @author Niklas Nieberler
 */

@Plugin(id = "server-registration", name = "server-registration", version = "1.0.1", authors = ["MrManHD"])
class VelocityPlugin @Inject constructor(
    server: ProxyServer,
    logger: Logger
) {

    private val kubernetesClient = KubernetesClientBuilder().build()
    private val serverRegisterManager = ServerRegisterManager(server, logger)
    private val configMapHandler = ConfigMapHandler(kubernetesClient)

    @Subscribe
    fun handleInitialize(event: ProxyInitializeEvent) {
        val config = this.configMapHandler.getConfig()
        config.serversToRegister
            .map { it.toServerInfo() }
            .forEach { this.serverRegisterManager.registerServer(it) }
    }

    private fun registerKubernetesInformers() {
        val informerFactory = this.kubernetesClient.informers()
        val podSharedIndexInformer = informerFactory.sharedIndexInformerFor(Pod::class.java, 600000L)
        podSharedIndexInformer.addEventHandler(PodResourceEventHandler(this.serverRegisterManager))
        informerFactory.startAllRegisteredInformers()
    }

}