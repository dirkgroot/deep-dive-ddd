package nl.avisi.socialnetwork.domain.contribution

interface ContributionRepository {
    fun nextContributionID(): ContributionID
    fun nextPostID(): PostID
    fun insert(contribution: Contribution)
    fun update(contribution: Contribution)
    fun findByID(contributionID: ContributionID): Contribution?
}
