package com.github.pool_party.resistance_bot.message

import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.Member
import com.github.pool_party.resistance_bot.utils.makeUserLink

// TODO add short description according to rules.
fun receiveSpyRole(otherSpies: List<String>) =
    """
    TODO:

    *Your role - the spy 🦹‍♂️*
    ${if (otherSpies.isEmpty()) "" else "Your teammates: ${otherSpies.joinToString { "`it`" }}" }
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

fun history(state: GameState) =
    """
    |*Previously in the game 📜*:
    |
    |${state.history.withIndex().joinToString("\n|") { "${it.index + 1}. ${it.value}"}}
    """.trimMargin()

fun leaderChooseMessage(size: Int) = """*Choose the best team for the upcoming mission! 👊* TODO: size: $size"""

const val VOTING_SUGGEST = """Head to the bot chat for the voting"""

// TODO Show the names of included players (in a beautiful way).
const val TEAM_VOTE = """Do you trust this team a mission? 🤨"""

// TODO Consider using different button marks for this vote.
const val MISSION_VOTE = """Make your choice 🤔"""
