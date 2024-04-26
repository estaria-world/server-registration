package world.estaria.server.registration.config

import kotlinx.serialization.Serializable

/**
 * @author Niklas Nieberler
 */

@Serializable
class Config(
    val serversToRegister: List<ServerRegisterInfo>
) {

    object Default {
        fun get(): Config {
            return Config(
                listOf(
                    ServerRegisterInfo(
                        "test",
                        "127.0.0.01",
                        1234
                    )
                )
            )
        }
    }

}