package teamcity.crowd.plugin

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.users.SUser
import teamcity.crowd.plugin.utils.GroupNameConverter
import teamcity.crowd.plugin.utils.LoggerFactory

open class GroupsUpdater(private val userGroupManager: UserGroupManager,
                         private val groupNameConverter: GroupNameConverter,
                         private val doNotRemoveInternalGroups: Boolean = true,
                         private val shouldCreateGroups: Boolean = true,
                         loggerFactory: LoggerFactory) {

    companion object {
        const val ALL_USERS_GROUP = "All Users"
        const val CREATED_BY_PLUGIN_MESSAGE = "Created by Crowd Plugin"
    }

    private val logger: Logger = loggerFactory.getServerLogger()


    fun updateGroups(teamCityUser: SUser, userGroupsInCrowd: Collection<String>) {
        val allTeamCityGroupsUserIsAMemberOfAlready = allTeamCityUserGroups(teamCityUser)

        val listOfGroupsUserShouldBeRemovedFrom = groupsToBeRemovedFrom(
                if (doNotRemoveInternalGroups) allTeamCityNonInternalUserGroups(teamCityUser) else allTeamCityGroupsUserIsAMemberOfAlready,
                userGroupsInCrowd)

        for (crowdGroup in userGroupsInCrowd) {
            if (!allTeamCityGroupsUserIsAMemberOfAlready.contains(crowdGroup)) {
                var teamCityGroup = userGroupManager.findUserGroupByName(crowdGroup)
                if (teamCityGroup == null && shouldCreateGroups) {
                    val groupKey = groupNameConverter.convert(crowdGroup)
                    if (groupKey != null) {
                        teamCityGroup = userGroupManager.createUserGroup(groupKey, crowdGroup, CREATED_BY_PLUGIN_MESSAGE)
                    }
                }

                if (teamCityGroup != null) {
                    teamCityGroup.addUser(teamCityUser)
                    logger.info("Added user [${teamCityUser.username}] to group [$crowdGroup]")
                }
            }
        }

        for (groupName in listOfGroupsUserShouldBeRemovedFrom) {
            logger.info("Removing user [${teamCityUser.username}] from group [$groupName]")
            val teamCityGroup = userGroupManager.findUserGroupByName(groupName)
            teamCityGroup?.removeUser(teamCityUser)
        }

        teamCityUser.userGroups
    }

    private fun allTeamCityUserGroups(teamCityUser: SUser): List<String> {
        return teamCityUser.userGroups.map { it.name }
    }

    private fun groupsToBeRemovedFrom(teamCityGroups: Collection<String>, crowdGroups: Collection<String>): List<String> {
        return teamCityGroups.filter {
            !crowdGroups.contains(it) && ALL_USERS_GROUP.toLowerCase() != it.toLowerCase()
        }
    }

    private fun allTeamCityNonInternalUserGroups(teamCityUser: SUser): List<String> {
        return teamCityUser.userGroups.filter { it.description == CREATED_BY_PLUGIN_MESSAGE }.map { it.name }
    }
}