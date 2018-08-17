package teamcity.crowd.plugin

import com.atlassian.crowd.model.user.User
import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.serverSide.auth.ServerPrincipal
import jetbrains.buildServer.users.UserModel
import teamcity.crowd.plugin.config.CrowdPluginConfiguration
import teamcity.crowd.plugin.utils.LoggerFactory
import jetbrains.buildServer.users.SUser


class LoggedInUserService(
        private val pluginCrowdClient: PluginCrowdClient,
        private val userModel: UserModel,
        private val groupsUpdater: GroupsUpdater,
        loggerFactory: LoggerFactory) {

    private val logger: Logger = loggerFactory.getServerLogger()

    fun updateMembership(crowdUser: User): ServerPrincipal {
        val tcUser = findUserAccountFor(crowdUser)
        val createdOrUpdatedUser = if (tcUser == null) {
            createUserAccount(crowdUser)
        } else {
            updateUserDetails(tcUser, crowdUser)
        }
        val userGroupsInCrowd = userGroupsInCrowd(createdOrUpdatedUser)
        groupsUpdater.updateGroups(createdOrUpdatedUser, userGroupsInCrowd)

        return ServerPrincipal(CrowdPluginConfiguration.CROWD_REALM, crowdUser.name)
    }


    private fun findUserAccountFor(user: User): SUser? {
        return userModel.findUserAccount(CrowdPluginConfiguration.CROWD_REALM, user.name)
    }

    private fun createUserAccount(user: User): SUser {
        logger.info("User [${user.name}] doesn't exists in TeamCity. Creating!")
        val userAccount = userModel.createUserAccount(CrowdPluginConfiguration.CROWD_REALM, user.name)
        userAccount.updateUserAccount(user.name, user.displayName, user.emailAddress)
        return userAccount
    }

    private fun updateUserDetails(teamCityUser: SUser, user: User): SUser {
        val associatedUser = teamCityUser.associatedUser
        if (associatedUser!!.name != user.displayName || associatedUser.email != user.emailAddress) {
            teamCityUser.updateUserAccount(user.name, user.displayName, user.emailAddress)
        }
        return teamCityUser
    }

    private fun userGroupsInCrowd(teamCityUser: SUser): List<String> {
        return pluginCrowdClient.getUserGroups(teamCityUser.username).map { it.name }
    }
}