package com.github.pool_party.resistance_bot.message

import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.Member
import com.github.pool_party.resistance_bot.state.RoundResult
import com.github.pool_party.resistance_bot.utils.makeUserLink

// TODO add short description according to rules.
fun receiveSpyRole(otherSpies: List<String>) =
    """
    TODO:

    *Your role - the spy 🦹‍♂️*
    ${if (otherSpies.isEmpty()) "" else "Your teammates: ${otherSpies.joinToString { "`it`" }}"}
    """.trimIndent()

// TODO add short description according to rules.
//  Rename the role, change emoji.
const val RECEIVE_RESISTANCE_ROLE = """*Your role - the resistant 🦸*"""

// TODO Store the order of results in the state for chronological roadmap.
//  Show all leaders in particular order.
//  Discuss overall concept of round summary.
fun roundSummary(state: GameState, leader: Member) =
    """
    TODO: state:

    ```
         score: resistance ${state.resistancePoints} - ${state.spyPoints} red spies
    rejections: ${state.squadRejections}
    ```

    next leader: ${makeUserLink(leader.name, leader.id)}
    """.trimIndent()

fun history(state: MutableList<RoundResult>) =
    if (state.isNotEmpty()) {
        """
        |*Previously in the game 📜*:
        |
        |${state.withIndex().joinToString("\n|") { "${it.index + 1}. ${it.value}" }}
        """.trimMargin()
    } else {
        """
        *Nothing to see... yet 👀*
        """.trimIndent()
    }

fun leaderChooseMessage(size: Int) = """*Choose the best team of $size members for the upcoming mission! 👊*"""

// TODO Singular form.
fun tagAfkPlayers(members: List<Member>) =
    """
    |*Still haven't made up their minds ⌛*:
    |
    |${members.joinToString("|\n") { "- ${makeUserLink(it.name, it.id)}" }}
    """.trimMargin()

const val ON_NON_PLAYER_STOP = """_Only active players can stop the game 🚫_"""

const val ON_ONGOING_STOP_VOTE = """Vote to stop a game is already going. Make your choice above 👆"""

fun stopTheGameVote(senderName: String?) =
    """${if (senderName != null) "$senderName" else "One of the players"} wants to stop the game 😲"""

const val ON_GAME_STOP = """Game is stopped... 😵"""

const val ON_STOP_VOTE_FAIL = """Not enough votes. The game continues 🥳"""

const val VOTING_SUGGEST = """Head to the bot chat for the voting"""

// TODO Show the names of included players (in a beautiful way).
const val TEAM_VOTE = """Do you trust this team a mission? 🤨"""

// TODO Show players against too.
val TEAM_APPROVED =
    """
    *Suggested team is approved by the most players 👍*

    Time to make your mission votes!
    """.trimIndent()

fun teamRejected(membersAgainst: List<Member>) =
    """
    |*Suggested team doesn't have player's trust 👎*
    |
    |Voted against:
    |${membersAgainst.joinToString("\n|") { "- ${makeUserLink(it.name, it.id)}" }}
    """.trimMargin()

// TODO Consider using different button marks for this vote.
const val MISSION_VOTE = """Make your choice 🤔"""

// TODO Show all members with the roles (lookup to mafia game).
fun gameResult(areSpiesWon: Boolean) =
    """
    TODO
    """.trimIndent()
