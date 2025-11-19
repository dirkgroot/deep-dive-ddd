package nl.avisi.socialnetwork.domain.contribution

import nl.avisi.socialnetwork.domain.user.UserRepository
import nl.avisi.socialnetwork.domain.user.Username

class ContributionVisibilityService(
    private val userRepository: UserRepository
) {
    fun canView(viewer: Username, contribution: Contribution): Boolean {
        val author = userRepository.findByUsername(contribution.openingPost.author)
            ?: throw IllegalStateException("Author not found")

        return !author.hasBlocked(viewer)
    }
}
