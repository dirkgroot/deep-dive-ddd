package nl.avisi.socialnetwork.infrastructure

import nl.avisi.socialnetwork.domain.user.User
import nl.avisi.socialnetwork.domain.user.UserRepository
import nl.avisi.socialnetwork.domain.user.Username
import org.springframework.stereotype.Repository

@Repository
class UserJDBCRepository : UserRepository {
    override fun findByUsername(username: Username): User? =
        if (username == Username("dirk"))
            User(username).apply { block(Username("stefan")) }
        else
            User(username)
}
