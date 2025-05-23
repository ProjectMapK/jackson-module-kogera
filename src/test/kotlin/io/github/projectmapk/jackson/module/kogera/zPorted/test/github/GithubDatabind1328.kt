package io.github.projectmapk.jackson.module.kogera.zPorted.test.github

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.projectmapk.jackson.module.kogera.defaultMapper
import io.github.projectmapk.jackson.module.kogera.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGithubDatabind1328 {
    @Test
    fun testPolymorphicWithEnum() {
        val invite = defaultMapper.readValue<Invite>(
            """|{
                   |  "kind": "CONTACT",
                   |  "to": {
                   |    "name": "Foo"
                   |  }
                   |}
            """.trimMargin()
        )

        assertEquals(InviteKind.CONTACT, invite.kind)
        assertEquals("Foo", (invite.to as InviteToContact).name)
    }

    data class Invite(
        val kind: InviteKind,
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "kind", visible = true)
        @JsonSubTypes(
            JsonSubTypes.Type(InviteToContact::class),
            JsonSubTypes.Type(InviteToUser::class)
        )
        val to: InviteTo
    )

    interface InviteTo

    @JsonTypeName("CONTACT")
    data class InviteToContact(
        val name: String? = null
    ) : InviteTo

    @JsonTypeName("USER")
    data class InviteToUser(
        val user: String
    ) : InviteTo

    enum class InviteKind {
        CONTACT,
        USER
    }
}
