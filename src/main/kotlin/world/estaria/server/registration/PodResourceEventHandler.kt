package world.estaria.server.registration

import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.client.informers.ResourceEventHandler

/**
 * @author Niklas Nieberler
 */

class PodResourceEventHandler(
    private val serverRegisterManager: ServerRegisterManager
) : ResourceEventHandler<Pod> {

    override fun onAdd(pod: Pod) {
        if (!PodRequirements.hasPodAllRequirements(pod))
            return
        this.serverRegisterManager.registerServer(pod)
    }

    override fun onUpdate(oldPod: Pod, newPod: Pod) {
        if (!PodRequirements.hasPodAllRequirements(newPod))
            return
        if (this.serverRegisterManager.isServerRegistered(newPod))
            return
        this.serverRegisterManager.registerServer(newPod)
    }

    override fun onDelete(pod: Pod, deletedFinalStateUnknown: Boolean) {
        val podName = pod.metadata.name
        this.serverRegisterManager.unregisterServer(podName)
    }

}