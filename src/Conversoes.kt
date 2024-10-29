import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.absoluteValue

/**
 * Converte um valor inteiro para binário, usando divisões sucessivas por 2 e
 * armazenando em uma pilha e depois desempilhando ela para obter os bits na ordem
 * correta.
 */
fun converteInteiroParaBinario(inteiro: BigInteger): String {
    var aux = inteiro
    val pilha = Stack<String>()
    var binario = ""

    while (aux > BigInteger.ZERO) {
        pilha.push((aux % BigInteger.TWO).toString())
        aux /= BigInteger.TWO
    }

    while (pilha.isNotEmpty()) {
        binario += if (pilha.pop() == "1") "1" else "0"
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
 *  por 2. Para somente quando o número é convetido ou o binário possui mais de 2048 casas, desconsiderando os primeiros 0s
 *  caso não haja casas ainda ocupadas, ou quando consegue ser convertido dentro da precisão. Avisa se houver um trucamento.
 */
fun converteFracionarioParaBinario(fracionario: BigDecimal): String {
    var binario = ""
    var aux = fracionario

    while (aux > BigDecimal.ZERO) {

        if (binario.length > 2048) {
//            100000
//            print("Meu coração mandou")
//            Thread.sleep(1000)
//            repeat(3) {
//                print(".")
//                Thread.sleep(1000)
//            }
//            println()
            return "$binario..."
        }
        aux *= BigDecimal.TWO

        if (aux >= BigDecimal.ONE) {
            binario += "1"
            aux = retiraParteInteira(aux)
        } else binario += "0"

    }

    return binario
}

/**
 * Subtrai 1 ao bit menos significativo. Se o valor do ultimo bit for 0 altera o valor do bit para 1
 * e chama recursivamente a função com o valor do ultimo bit menos 1, faz isso até encontrar 1
 * alterando o valor do bit para 0. Se não encontrar um bit com valor 1, imprime uma mensagem
 * e retorna o valor do binario até o momento.
 */
fun subtraiBit(binario: MutableList<Char>, ultimo: Int): String {
    if (ultimo < 0) {
        println("Não foi possível fazer a subtração, devido ao bit ser o mais significativo")
        return binario.joinToString("")
    }
    if (binario[ultimo] == '1') {
        binario[ultimo] = '0'
    } else {
        binario[ultimo] = '1'
        subtraiBit(binario, ultimo - 1)
    }
    return binario.joinToString("")
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
        return binario.joinToString("")
    }

    if (binario[ultimo] == '0') binario[ultimo] = '1'
    else {
        binario[ultimo] = '0'
        somaBit(binario, ultimo - 1)
    }

    return binario.joinToString("")
}

/**
 * Retira complemento de 2 subtraindo 1 bit do valor da mantissa e aplicando complemento de 1
 */
fun retiraComplementoDe2(binario: String): String {
    var mantissa = binario.substring(startIndex = 1, endIndex = binario.indexOf("*"))
    val expoente = binario.substring(startIndex = binario.indexOf("*"))

    mantissa = subtraiBit(mantissa.toMutableList(), mantissa.length - 1)
    return aplicaComplementoDe1(".$mantissa$expoente")
}

/**
 * Aplica complemento de 1 trocando os bit de valor 1 para 0 e 0 para 1.
 */
fun aplicaComplementoDe1(binario: String): String {
    val mantissa = binario.substring(startIndex = 0, endIndex = binario.indexOf("*"))
    val expoente = binario.substring(startIndex = binario.indexOf("*"))

    return mantissa.map { bit ->
        if (bit == '1') '0'
        else if (bit == '0') '1'
        else bit
    }.joinToString("") + expoente
}

/**
 * Aplica complemento de 1 e adiciona 1 ao bit menos representativo.
 */
fun aplicaComplementoDe2(numero: String): String {
    val numeroC1 = aplicaComplementoDe1(numero)
    val mantissa = numeroC1.substring(startIndex = 1, endIndex = numeroC1.indexOf("*"))
    val expoente = numeroC1.substring(startIndex = numeroC1.indexOf("*"))
    val numeroC2 = somaBit(mantissa.toMutableList(), mantissa.length - 1)

    return ".$numeroC2$expoente"
}

/**
 *  Converte binario normalizado retirando complemento de 2, se necessário, e os valores
 *  da mantissa e expoente, depois flutua o ponto para a posição indicada pelo expoente.
 *  Após desnormalizar, converte para Decimal o binário encontrado.
 */
fun converteBinarioNormalizadoParaDecimal(binario: String): String {
    val binarioProcessado = if (sinal.contains("-")) retiraComplementoDe2(binario) else binario
    val mantissa = binarioProcessado
        .substring(startIndex = 0, endIndex = binarioProcessado.indexOf("*"))
        .replace(".", "").toMutableList()
    val expoente = binarioProcessado.substring(startIndex = binarioProcessado.indexOf("(") + 1, endIndex = binarioProcessado.indexOf(")")).toInt()

    val binarioDesnormalizado = flutuaPonto(expoente, mantissa)

    return converteBinarioParaDecimal(binarioDesnormalizado)
}

/**
 * Se o expoente for maior que zero, adiciona zeros ao final do binário até que o tamanho do binário
 * seja igual ao valor do expoente, após isso coloca o ponto no indice indicado pelo valor do expoente.
 * Se o expoente for menor que zero, adiciona zeros no início do binárioo número de vezes igual ao valor
 * absoluto do expoente e ao final adiciona "0.". Se o expoente for zero, apenas adiciona "0." no início.
 */
fun flutuaPonto(expoente: Int, binario: MutableList<Char>): String {
    if (expoente > 0) {
        while (binario.size < expoente) binario.addLast('0')
        binario.add(expoente, '.')
    } else if (expoente < 0) {
        repeat(expoente.absoluteValue) { binario.addFirst('0') }
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
 * que foi passado um valor inteiro, adiciona ".0" ao final dele.
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

/**
 * Esta função pré-processa o número decimal, separa as partes inteira e fracionária,
 * armazena a parte inteira em um BigInteger e a parte fracionária em um BigDecimal.
 * Em seguida, converte a parte inteira e a fracionaria para binário, separadamente.
 * Ao final, retorna o binário encontrado.
 */
fun converteDecimalParaBinario(decimal: String): String {
    val decimalProcessado = preprocessaDecimal(decimal)
    val partes = separaPartesPeloPonto(decimalProcessado)
    val parteInteira = BigInteger(partes.first)
    val parteFracionaria = BigDecimal(partes.second)
    val inteiroBinario = converteInteiroParaBinario(parteInteira)
    var binario = inteiroBinario
    val fracionarioBinario = converteFracionarioParaBinario(parteFracionaria)
    binario += ".$fracionarioBinario"

    return binario
}

/**
 * Converte binario inteiro invertendo os bits do binario e elevando 2 ao indice para cada
 * bit igual a 1. Retorna o decimal
 */
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

/**
 * Converte binario fracionário dividindo 1 por 2 elevado ao indice + 1 para cada
 * bit igual a 1. Retorna o decimal.
 */
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

/**
 * Converte o binario separando as partes dele em inteiro e fracionario e convertendo elas
 * separadamente. Retorna a soma do resultado das duas conversões.
 */
fun converteBinarioParaDecimal(binario: String): String {
    val partes = separaPartesPeloPonto(binario)
    val parteInteira = partes.first
    val parteFracionaria = partes.second

    val decimalInteiro = converteBinarioInteiroParaDecimal(parteInteira)
    val decimalFracionario = converteBinarioFracionarioParaDecimal(parteFracionaria)
    return "${decimalInteiro + decimalFracionario}"
}
