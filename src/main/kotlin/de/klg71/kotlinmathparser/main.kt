package de.klg71.kotlinmathparser

fun main() {
    println("## Kotlin math parser ##")
    println("Enter your term:")
    val input = readLine()!!

    mutableListOf<Int>().apply {
        addAll(input.chars().toArray().asList())
    }.let {
        tokenize(it)
    }.let {
        println(evaluate(it))
    }


}

private fun evaluate(tokens: List<Token>) =
        tokens.braces().let {
            sign(it)
        }.run {
            mathOperation<Mul> { op1, op2 -> op1 * op2 }()
        }.run {
            mathOperation<Plus> { op1, op2 -> op1 + op2 }()
        }.run {
            mathOperation<Minus> { op1, op2 -> op1 - op2 }()
        }.let {
            numberAt(it, 0)
        }


private fun sign(tokens: List<Token>): List<Token> {
    tokens.indexOfFirst { it is Minus }.let {
        return when (it) {
            0 -> listOf(Number(-numberAt(tokens, it + 1))) + tokens.drop(2)
            else -> tokens
        }
    }
}


private fun List<Token>.braces(): List<Token> {
    indexOfFirst { it is BraceOpen }.let {
        return when (it) {
            -1 -> this
            else -> {
                val closedIndex = indexOfLast { it is BraceClosed }
                if(closedIndex==-1){
                    throw java.lang.RuntimeException("No closing brace for openBrace at position: $it !")
                }
                val result = evaluate(subList(it + 1,closedIndex ))
                (subList(0, it) + listOf(Number(result)) + subList(closedIndex + 1, size)).braces()
            }

        }
    }
}

private inline fun <reified T : Token> mathOperation(crossinline operation: (op1: Int, op2: Int) -> Int): List<Token>.() -> List<Token> {
    var resultOperation:(List<Token>.()->List<Token>)? = null
    resultOperation = {
        indexOfFirst { it is T }.let {
            when (it) {
                -1 -> this
                0 -> resultOperation!!(drop(1))
                else -> {
                    val result = operation(numberAt(this, it - 1), numberAt(this, it + 1))
                    resultOperation!!(replaceWithResult(this, it, result))
                }
            }
        }

    }
    return resultOperation
}

private fun replaceWithResult(tokens: List<Token>, it: Int, sum: Int) =
        tokens.subList(0, it - 1) + listOf(Number(sum)) + tokens.subList(it + 2, tokens.size)

private fun numberAt(tokens: List<Token>, index: Int) =
        tokens[index].let {
            if (it is Number) {
                it.number
            } else {
                throw RuntimeException("Expected number at position: $index but got ${tokens[index]}")
            }
        }


