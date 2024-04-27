package world.estaria.server.registration

import io.fabric8.kubernetes.api.model.Pod
import java.net.InetAddress

/**
 * @author Niklas Nieberler
 */

object PodRequirements {

    private val hostAddress = InetAddress.getLocalHost().hostAddress

    fun hasPodAllRequirements(pod: Pod): Boolean {
        val podIP = pod.status.podIP ?: return false
        if (pod.metadata.namespace != KUBERNETES_NAMESPACE)
            return false
        if (podIP == this.hostAddress)
            return false
        return isControllerMinecraftServer(pod)
    }

    private fun isControllerMinecraftServer(pod: Pod): Boolean {
        return pod.metadata.ownerReferences[0].kind == "MinecraftServer"
    }

}