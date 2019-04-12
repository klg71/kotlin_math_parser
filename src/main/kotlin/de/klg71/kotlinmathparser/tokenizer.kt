package de.klg71.kotlinmathparser

interface Token

data class Number(val number: Int) : Token

class Plus : Token
class Minus : Token
class Mul : Token
class BraceOpen:Token
class BraceClosed:Token


fun tokenize(chars: List<Int>): List<Token> {
    if (chars.isEmpty()) {
        return emptyList()
    }
    if (chars.first().toChar().isDigit()) {
        val number = getNumber(chars)
        return listOf(Number(number.toInt())) + tokenize(chars.drop(number.length))
    }

    return when (chars.first().toChar()) {
        '+' -> listOf(Plus()) + tokenize(chars.drop(1))
        '-' -> listOf(Minus()) + tokenize(chars.drop(1))
        '*' -> listOf(Mul()) + tokenize(chars.drop(1))
        '(' -> listOf(BraceOpen()) + tokenize(chars.drop(1))
        ')' -> listOf(BraceClosed()) + tokenize(chars.drop(1))
        ' ' -> tokenize(chars.drop(1))
        else -> throw RuntimeException("Invalid token")
    }
}

private fun getNumber(chars: List<Int>): String {
    if (chars.isEmpty()) {
        throw RuntimeException("Invalid token")
    }

    return StringBuilder().run {
        chars.takeWhile { it.toChar().isDigit() }.forEach {
            append(it.toChar())
        }
        toString()
    }
}

