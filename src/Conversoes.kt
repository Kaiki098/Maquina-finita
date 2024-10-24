import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.absoluteValue

/**
 * Converte um valor inteiro para binário, usando divisões sucessivas por 2 e
 * armazenando em uma pilha e depois desempilhando ela para obter os bits na ordem
 * correta. Pode retornar o valor já truncado.
 */
fun converteInteiroParaBinario(inteiro: BigInteger): String {
    var aux = inteiro
    val pilha = Stack<String>()
    var binario = ""

    while (aux > BigInteger.ZERO) { // Converte completamente o inteiro
        pilha.push((aux % BigInteger.TWO).toString())
        aux /= BigInteger.TWO
    }

    while (pilha.isNotEmpty()) {
        binario += if (pilha.pop() == "1") "1" else "0"
    }

    if (binario.length > precisao) {
        // FIXME Estudar a adição de um arredondamento aqui
        println("Houve um truncamento")
        return binario.substring(startIndex = 0, endIndex = precisao)
    }

    return binario
}

/**
 * Quebra o número pelo caractere "." e retorna as partes inteira e fracionária, respectivamente.
 */
fun separaPartesPeloPonto(numero: String): Pair<String, String> {
    val numeroQuebrado = numero.split('.')
    return Pair(numeroQuebrado[0], ".${numeroQuebrado[1]}")
}

/**
 * Retira a parte inteira de um número decimal, retornando apenas a parte fracionária.
 */
fun retiraParteInteira(numero: BigDecimal) =
    BigDecimal(separaPartesPeloPonto(numero.toString()).second)

/**
 *  Converte partes fracionárias de números decimais para números binários usando multiplicações sucessivas
 *  por 2. Para somente quando o número ocupa todas as casas disponíveis da mantissa, desconsiderando os primeiros 0s
 *  caso não haja casas ainda ocupadas, ou quando consegue ser convertido dentro da precisão.
 */
fun converteFracionarioParaBinario(fracionario: BigDecimal, casasOcupadas: Int): String {
    var binario = ""
    var aux = fracionario
    var casasDisponiveis =
        precisao - casasOcupadas // Variável usada para controlar o número de casas para preencher a parte fracionária

    while (aux != BigDecimal.ZERO) {
        aux *= BigDecimal.TWO
        if (aux >= BigDecimal.ONE) {
            binario += "1"
            aux = retiraParteInteira(aux)
        } else {
            val numeroDeZerosNoInicio = casasDisponiveis - precisao
            if (casasOcupadas == 0 && numeroDeZerosNoInicio == binario.length) {
                // É feito essa checagem para números muito pequenos em que a precisão não comporta as casas.
                // Ex: 0000001, precisao = 3. Para isso são desconsiderados os primeiros 0s aumentando-se o
                // valor de casasDisponiveis.
                binario += "0"
                casasDisponiveis++
            } else binario += "0"
        }
        if (binario.length == casasDisponiveis) {
            if (aux > BigDecimal.ZERO) println("Houve um truncamento.")
            break
        }
    }

    return binario
}

/**
 * Soma 1 ao bit menos significativo. Se o valor do ultimo bit for 1 altera o valor do bit para 0
 * e chama recursivamente a função com o valor do ultimo bit menos 1, faz isso até encontrar 0
 * alterando o valor do bit para 1. Se não encontrar um bit com valor 0, imprime uma mensagem
 * e retorna o valor do binario até o momento.
 */
fun somaBit(binario: MutableList<Char>, ultimo: Int): String {
    if (ultimo < 0) {
        println("Não foi possível fazer a soma, devido ao bit ser o mais significativo")
        // TODO Talvez tacar uma exceção aqui
        return binario.toString()
    }

    if (binario[ultimo] == '0') {
        binario[ultimo] = '1'
    } else {
        binario[ultimo] = '0'
        somaBit(binario, ultimo - 1)
    }

    return binario.toString()
}

/**
 * Aplica complemento de 1 trocando os bit com valor 1 para 0 e 0 para 1.
 */
fun aplicaComplementoDe1(binario: String): String {
    return binario.map { bit ->
        if (bit == '1') '0'
        else if (bit == '0') '1'
        else bit
    }.joinToString("")
}

/**
 * Aplica complemento de 1 e adiciona 1 ao bit menos representativo.
 */
fun aplicaComplementoDe2(numero: String): String {
    val numeroC1 = aplicaComplementoDe1(numero)
    // val numeroC2 = somaBit(numeroC1.toMutableList(), numeroC1.length-1) FIXME
    return numeroC1
}

fun desaplicaComplementoDe2(binario: String): String {
    // TODO
    return binario
}

fun converteBinarioNormalizadoParaDecimal(binario: String): String {
    val mantissa = binario.substring(startIndex = 0, endIndex = binario.indexOf("*"))
    val binarioNormalizado = mantissa.replace("0.", "").toMutableList()
    var expoente = binario.substring(startIndex = binario.indexOf("(") + 1, endIndex = binario.indexOf(")")).toInt()

    while (binarioNormalizado.size < precisao) {
        binarioNormalizado.addFirst('0')
        expoente++
    }

    var binarioDesnormalizado = flutuaPonto(expoente, binarioNormalizado)
    if (sinal.contains("-")) {
        binarioDesnormalizado = aplicaComplementoDe2(binarioDesnormalizado)
    }

    return converteBinarioParaDecimal(binarioDesnormalizado)
}

fun flutuaPonto(expoente: Int, binario: MutableList<Char>): String {
    if (expoente > 0) {
        while (binario.size < expoente) binario.addLast('0')
        binario.add(expoente, '.')
    } else if (expoente < 0) {
        for (i in 0..<expoente.absoluteValue) binario.addFirst('0')
        binario.addFirst('.')
        binario.addFirst('0')
    } else {
        binario.addFirst('.')
        binario.addFirst('0')
    }
    return binario.joinToString("")
}

/**
 * Função responsável por armazenar o sinal do decimal, retirar o sinal,
 * substituir "," por "." e caso o binário não possuir um ponto, indicando
 * que foi passado um valor inteiro, adiciona-se ".0" ao final dele
 */
fun preprocessaDecimal(decimal: String): String {
    var decimalProcessado = decimal
    if (decimalProcessado.contains("-")) {
        decimalProcessado = decimalProcessado.replace("-", "")
        sinal = "-"
    } else sinal = ""

    decimalProcessado = decimalProcessado.replace(",", ".")
    if (!decimalProcessado.contains(".")) decimalProcessado += ".0"

    return decimalProcessado
}

// FIXME texto está explicando errado

/**
 * Essa função pré-processa o decimal, separa as partes inteira e fracionária, salva o valor da parte inteira
 * em um BigInteger, converte o valor inteiro, verifica se o valor encontrado é vazio, caso isso seta o valor da
 * string como "0.". Após isso, verifica se todos os bits da mantissa foram preenchidos, se não tiverem sido
 * preenchidos, salva a parte fracionaria em um BigDecimal, converte o valor e adiciona o binário resultante.
 * Por último, usa a variável global sinal para saber se o número é negativo, se for aplica complemento de dois.
 */
fun converteDecimalParaBinario(decimal: String): String {
    val decimalProcessado = preprocessaDecimal(decimal)
    val partes = separaPartesPeloPonto(decimalProcessado)
    val parteInteira = BigInteger(partes.first)
    val parteFracionaria = BigDecimal(partes.second)
    var inteiroBinario = converteInteiroParaBinario(parteInteira)
    if (inteiroBinario.isEmpty()) {
        inteiroBinario = "0"
    }
    var binario = "$inteiroBinario."

    if (binario.length - 1 < precisao) {
        val fracionarioBinario = converteFracionarioParaBinario(parteFracionaria, binario.length - 1)
        binario += fracionarioBinario
    } else if (parteFracionaria > BigDecimal.ZERO) {
        println("Houve um truncamento.")
    }

    if (sinal.contains("-")) {
        binario = aplicaComplementoDe2(binario)
    }
    // n = 8, l = -5, u = 5, -163,63 dá overflow, mas não deveria

    return binario
}

fun converteBinarioInteiroParaDecimal(binInteiro: String): BigDecimal {
    val bits = binInteiro.split("").filterNot { it.isEmpty() }
    val bitsInvertidos = bits.reversed()
    var decimal = BigDecimal("0")
    for (i in bitsInvertidos.indices) {
        if (bitsInvertidos[i] == "1") {
            decimal += BigDecimal.TWO.pow(i)
        }
    }
    return decimal
}

fun converteBinarioFracionarioParaDecimal(binFracionario: String): BigDecimal {
    val bits = binFracionario
        .replace(".", "")
        .split("")
        .filterNot { it.isEmpty() }

    var decimal = BigDecimal("0")
    for (i in bits.indices) {
        if (bits[i] == "1") {
            decimal += BigDecimal.ONE.divide(BigDecimal.TWO.pow((i + 1)))
        }
    }
    return decimal
}

fun converteBinarioParaDecimal(binario: String): String {
    val partes = separaPartesPeloPonto(binario)
    val parteInteira = partes.first
    val parteFracionaria = partes.second

    val decimalInteiro = converteBinarioInteiroParaDecimal(parteInteira)
    val decimalFracionario = converteBinarioFracionarioParaDecimal(parteFracionaria)
    return "${decimalInteiro + decimalFracionario}"
}
