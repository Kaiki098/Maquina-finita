import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.sin

fun converteInteiroParaBinario(inteiro: BigInteger): String {
    var aux = inteiro
    val pilha = Stack<String>()


    while (aux > BigInteger.ZERO && pilha.size < precisao) {
        pilha.push((aux % BigInteger.TWO).toString())
        aux /= BigInteger.TWO
    }

    var binario = ""
    while (pilha.isNotEmpty()) {
        if (pilha.pop() == "1") binario += "1"
        else binario += "0"
    }
    if (aux > BigInteger.ZERO) {
        println("Houve um Overflow")
        val posicaoPrimeiroBitRepresentativo = binario.indexOf("1")
        if (posicaoPrimeiroBitRepresentativo != -1) return binario.substring(posicaoPrimeiroBitRepresentativo)
        else return ""
    }

    return binario
}

fun retiraParteInteira(numero: BigDecimal) =
    BigDecimal(separaPartes(numero.toString()).second)

fun converteFracionarioParaBinario(fracionario: BigDecimal, casasOcupadas: Int): String {
    var binario = ""
    var aux = fracionario
    var casasDisponiveis = precisao - casasOcupadas

    try {
        while (aux != BigDecimal.ZERO) {
            aux *= BigDecimal.TWO
            if (aux >= BigDecimal.ONE) {
                binario += "1"
                aux = retiraParteInteira(aux)
            } else {
                val numeroDeZerosNoInicio = casasDisponiveis - precisao
                if (casasOcupadas == 0 && numeroDeZerosNoInicio == binario.length) {
                    binario += "0"
                    casasDisponiveis++
                } else binario += "0" // FIXME Quando o valor é muito pequeno os primeiro zeros são desconsiderados
            }
            if (binario.length == casasDisponiveis) {
                if (aux > BigDecimal.ZERO) println("Houve um truncamento") // somaBit()
                break
            }
        }
        return binario
    } catch (e: Exception) {
        println("Não foi possível converter a parte fracionário do número decimal para binário devido ao erro: $e")
        return ""
    }
}

fun aplicaComplementoDe1(binario: String): String {
    return  binario.map { bit ->
        if (bit == '1') '0'
        else if (bit == '0') '1'
        else bit
    }.joinToString("")
}

fun aplicaComplementoDe2(numero: String): String {
    val numeroC1 = aplicaComplementoDe1(numero)
    println("Complemento de um: $numeroC1")
    val numeroC2 = numeroC1
    // TODO
    return numeroC2
}

fun converteBinarioNormalizadoParaDecimal(binario: String): String {
    val mantissa = binario.substring(startIndex = 0, endIndex = binario.indexOf("*"))
    val binarioNormalizado = mantissa.replace("0.", "").toMutableList()
    var expoente = binario.substring(startIndex = binario.indexOf("(")+1, endIndex = binario.indexOf(")")).toInt()

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
    /**
     * 1.010
     * 0.1
     * 0.10
     */

    if (expoente > 0) {
        while (binario.size < expoente) binario.addLast('0')
        binario.add(expoente, '.')
    } else if (expoente < 0) {
        for (i in 0..<expoente.absoluteValue) binario.addFirst('0')
        binario.addFirst('.')
        binario.addFirst('0')
    } else {
        binario.add(1, '.')
    }


    return binario.joinToString("")
}

fun converteDecimalParaBinario(decimal: String): String {

    if (decimal.contains("-")) sinal = "-" else sinal = ""
    val partes = separaPartes(decimal.replace("-", ""))


    val parteInteira = if (partes.first.isNotEmpty()) BigInteger(partes.first) else BigInteger.ZERO

    val inteiroBinario = converteInteiroParaBinario(parteInteira) + "."
    var binario = inteiroBinario // FIXME TA DANDO ERRO AQUItirar os zeros da direita
    // FIXME E se o número inteiro der exatamente o número de casas da precisão?
    //  Talvez só verificar se o fracionario é diferente de 0 e se a ultima casa do inteiro é 1

    if (binario.first() == '.') {
        binario = binario.replace(".", "0.")
    }

    if (inteiroBinario.length - 1 < precisao) {
        val parteFracionaria = BigDecimal(partes.second)
        val fracionarioBinario = converteFracionarioParaBinario(parteFracionaria, inteiroBinario.length-1)
        if (binario.isNotEmpty()) binario += fracionarioBinario else binario = "0.$fracionarioBinario" // FIXME forma de formatar ta feia
    }

    if (sinal.contains("-")) {
        println("Binario sem complemento: $binario")
        binario = aplicaComplementoDe2(binario)
    }
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
            decimal += BigDecimal.ONE.divide(BigDecimal.TWO.pow((i+1)))
        }
    }
    return decimal
}

fun converteBinarioParaDecimal(binario: String): String {
    val partes = separaPartes(binario) // FIXME Por para aplicar C2 aqui
    val parteInteira = partes.first
    val parteFracionaria = partes.second

    val decimalInteiro = converteBinarioInteiroParaDecimal(parteInteira)
    val decimalFracionario = converteBinarioFracionarioParaDecimal(parteFracionaria)
    return "${decimalInteiro + decimalFracionario}"
}
