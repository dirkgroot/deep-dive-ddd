package nl.avisi.socialnetwork.domain.user

interface UserRepository {
    fun findByUsername(username: Username): User?
}
