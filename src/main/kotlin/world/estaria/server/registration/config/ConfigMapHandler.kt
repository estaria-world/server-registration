package world.estaria.server.registration.config

import io.fabric8.kubernetes.client.KubernetesClient
import world.estaria.kube.configmap.kit.KubeConfigMapKit
import world.estaria.server.registration.KUBERNETES_NAMESPACE

/**
 * @author Niklas Nieberler
 */

class ConfigMapHandler(
    kubernetesClient: KubernetesClient
) {

    private val configSerializer = Config.serializer()
    private val configName = "server-registration.yaml"
    private val configMapManager = KubeConfigMapKit.initializeKubeConfig(KUBERNETES_NAMESPACE, kubernetesClient)

    init {
        if (!this.configMapManager.existsConfig(configName)) {
            this.configMapManager.createConfigMap(configName, configSerializer, Config.Default.get())
        }
    }

    fun getConfig(): Config {
        return this.configMapManager.getConfig(this.configName, this.configSerializer)
            ?: throw NullPointerException("failed to find $configName")
    }

}