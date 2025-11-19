package nl.avisi.socialnetwork.application

import nl.avisi.socialnetwork.domain.contribution.Contribution
import nl.avisi.socialnetwork.domain.contribution.ContributionID
import nl.avisi.socialnetwork.domain.contribution.ContributionRepository
import nl.avisi.socialnetwork.domain.user.Username
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.UUID

@RestController
@RequestMapping("/contributions")
class ContributionController(
    private val repository: ContributionRepository
) {
    @PostMapping
    fun newContribution(username: String, @RequestBody content: RichTextModel): ResponseEntity<UUID> {
        val postContent = content.asRichText()
        val contribution = Contribution.newContribution(
            repository.nextContributionID(),
            repository.nextPostID(),
            Username(username), OffsetDateTime.now(), postContent
        )
        repository.insert(contribution)

        return ResponseEntity.ok(contribution.id.asUUID())
    }

    @GetMapping("/{id}")
    fun findByID(@PathVariable id: String): ResponseEntity<Contribution> {
        val contribution = repository.findByID(ContributionID(UUID.fromString(id)))

        return if (contribution == null)
            ResponseEntity.notFound().build()
        else
            ResponseEntity.ok(contribution)
    }
}
