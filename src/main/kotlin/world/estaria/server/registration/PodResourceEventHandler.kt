package world.estaria.server.registration

import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.client.informers.ResourceEventHandler
import java.net.InetAddress

/**
 * @author Niklas Nieberler
 */

class PodResourceEventHandler(
    private val serverRegisterManager: ServerRegisterManager
) : ResourceEventHandler<Pod> {

    private val hostAddress = InetAddress.getLocalHost().hostAddress

    override fun onAdd(pod: Pod) {
        if (!hasPodAllRequirements(pod))
            return
        val podIp = pod.status.podIP
        val podName = pod.metadata.name
        this.serverRegisterManager.registerServer(podName, podIp)
    }

    override fun onUpdate(oldObj: Pod, newObj: Pod) {}

    override fun onDelete(pod: Pod, deletedFinalStateUnknown: Boolean) {
        if (!hasPodAllRequirements(pod))
            return
        val podName = pod.metadata.name
        this.serverRegisterManager.unregisterServer(podName)
    }

    private fun hasPodAllRequirements(pod: Pod): Boolean {
        if (pod.metadata.namespace != KUBERNETES_NAMESPACE)
            return false
        if (pod.status.podIP == this.hostAddress)
            return false
        return true
    }
}