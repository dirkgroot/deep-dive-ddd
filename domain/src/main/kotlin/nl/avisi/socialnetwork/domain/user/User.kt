package nl.avisi.socialnetwork.domain.user

class User(
    val username: Username
) {
    private val blockedUsers: MutableSet<Username> = mutableSetOf()

    fun block(username: Username) {
        blockedUsers.add(username)
    }

    fun hasBlocked(username: Username) = blockedUsers.contains(username)
}
