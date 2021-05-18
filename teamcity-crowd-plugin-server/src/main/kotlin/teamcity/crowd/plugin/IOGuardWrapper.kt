package teamcity.crowd.plugin

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.util.FuncThrow

/**
 * TeamCity secondary nodes run under the security manager which prohibits network calls, file modifications and start of the processes by default.
 * This is done to prevent modification of a state in external services or to prevent simultaneous file modification access
 * to files under the shared data directory (all TeamCity nodes share the same data directory).
 *
 * <br>
 * If network operation is safe (for instance, the operation only reads data, and does not change the state),
 * then to allow it on secondary nodes it should be wrapped into the IOGuard call.
 *
 * <br>
 * The class here works as a wrapper on top of the IOGuard. Since older TeamCity versions did not have the IOGuard class
 * in order to continue support them we have to use Java reflection.
 *
 * <br>
 * See also: https://plugins.jetbrains.com/docs/teamcity/plugin-development-faq.html#How+to+adapt+plugin+for+secondary+node
 * and https://www.jetbrains.com/help/teamcity/multinode-setup.html
 *
 */
object IOGuardWrapper {
    private val ioGuardClass: Class<*>?
    private val logger: Logger = Logger.getInstance(IOGuardWrapper::class.qualifiedName)

    init {
        ioGuardClass = getIOGuardClass()
    }

    private fun getIOGuardClass(): Class<*>? {
        return try {
            Class.forName("jetbrains.buildServer.serverSide.IOGuard")
        } catch (e: Exception) {
            null
        }
    }

    fun <R, E : Throwable?> allowNetworkCall(func: FuncThrow<R, E>): R {
        if (ioGuardClass == null) {
            // this server does not have the IOGuard class yet
            return func.apply()
        }

        try {
            val method = ioGuardClass.getMethod("allowNetworkCall", FuncThrow::class.java)
            return method.invoke(func) as R
        } catch (e: Exception) {
            // for some reason there is no allowNetworkCall in this class
            logger.debug("Could not find a method with name allowNetworkCall in the class: " + ioGuardClass.name)
            return func.apply()
        }
    }
}