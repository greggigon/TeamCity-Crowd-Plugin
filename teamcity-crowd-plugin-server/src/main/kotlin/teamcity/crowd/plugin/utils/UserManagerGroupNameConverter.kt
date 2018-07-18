package teamcity.crowd.plugin.utils

import jetbrains.buildServer.groups.UserGroupManager

interface GroupNameConverter {
    fun convert(groupName: String): String?
}

class UserManagerGroupNameConverter(private val userGroupManager: UserGroupManager) : GroupNameConverter {

    override fun convert(groupName: String): String? {
        var sanitizedGroupName = groupName.toUpperCase().replace(" ", "_").replace("-", "_")
        if (sanitizedGroupName.length < 16) {
            return sanitizedGroupName
        }
        sanitizedGroupName = sanitizedGroupName.substring(0, 16)
        return if (userGroupManager.findUserGroupByKey(sanitizedGroupName) == null) {
            sanitizedGroupName
        } else {
            findAlternative(sanitizedGroupName)
        }
    }


    private fun findAlternative(sanitizedGroupName: String): String? {
        val prefix = sanitizedGroupName.substring(0, 13)
        for (i in 0..999) {
            val newName = "$prefix${"%03d".format(i)}"
            if (userGroupManager.findUserGroupByKey(newName) == null) {
                return newName
            }
        }
        return null
    }
}