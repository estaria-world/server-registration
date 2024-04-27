package world.estaria.server.registration.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyReloadEvent
import io.fabric8.kubernetes.client.KubernetesClient
import world.estaria.server.registration.KUBERNETES_NAMESPACE
import world.estaria.server.registration.PodRequirements
import world.estaria.server.registration.ServerRegisterManager
import world.estaria.server.registration.config.ConfigMapHandler

/**
 * @author Niklas Nieberler
 */

class ProxyReloadListener(
    private val kubernetesClient: KubernetesClient,
    private val configMapHandler: ConfigMapHandler,
    private val serverRegisterManager: ServerRegisterManager
) {

    @Subscribe
    fun handleProxyReload(event: ProxyReloadEvent) {
        this.configMapHandler.updateConfig()

        val serverInfos = this.configMapHandler.getConfig().serversToRegister
            .map { it.toServerInfo() }
        serverInfos
            .filter { !this.serverRegisterManager.isServerRegistered(it) }
            .forEach { this.serverRegisterManager.registerServer(it) }

        getServerInfosToUnregister(serverInfos.map { it.name }).forEach {
            this.serverRegisterManager.unregisterServer(it)
        }
    }

    private fun getServerInfosToUnregister(serverInfoNames: List<String>): List<String> {
        return this.serverRegisterManager.getServers()
            .map { it.name }
            .filter { !getKubernetesPodNames().contains(it) }
            .filter { !serverInfoNames.contains(it) }
    }

    private fun getKubernetesPodNames(): List<String> {
        return this.kubernetesClient.pods()
            .inNamespace(KUBERNETES_NAMESPACE)
            .list()
            .items
            .filter { PodRequirements.hasPodAllRequirements(it) }
            .map { it.metadata.name }
    }

}