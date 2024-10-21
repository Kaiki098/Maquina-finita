import java.math.BigDecimal
import java.util.*
import kotlin.math.pow

fun converteInteiroParaBinario(inteiro: Long): String {
    var aux = inteiro
    val pilha = Stack<String>()
    while (aux > 0 && pilha.size < precisao) {
        pilha.push((aux % 2).toString())
        aux /= 2
    }

    var binario = ""
    while (pilha.isNotEmpty()) {
        if (pilha.pop() == "1") binario += "1"
        else binario += "0"
    }
    if (aux > 0) {
        println("Houve um Overflow")
        val posicaoPrimeiroBitRepresentativo = binario.indexOf("1")
        if (posicaoPrimeiroBitRepresentativo != -1) return binario.substring(posicaoPrimeiroBitRepresentativo)
        else return ""
    }

    return binario
}

fun retiraParteInteira(numero: Double) =
    separaPartes(numero.toString()).second.toDouble()

fun converteFracionarioParaBinario(fracionario: Double, casasOcupadas: Int): String {
    var binario = ""
    var aux = fracionario
    var casasDisponiveis = precisao - casasOcupadas
    try {
        while (aux != 0.0) {
            aux *= 2
            if (aux >= 1) {
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
                if (aux > 0) println("Houve um trucamento") // somaBit()
                break
            }
        }
        return binario
    } catch (e: Exception) {
        println("Não foi possível converter a parte fracionário do número decimal para binário devido ao erro: $e")
        return ""
    }
}

fun aplicaComplementoDe2(numero: String): String {
    // TODO
    return numero
}

fun converteBinarioNormalizadoParaDecimal(binario: String): String {
    val mantissa = binario.substring(startIndex = 0, endIndex = binario.indexOf("*"))
    val binarioNormalizado = mantissa.replace("0.", "").toMutableList()
    var expoente = binario.substring(startIndex = binario.indexOf("(")+1, endIndex = binario.indexOf(")")).toInt()

    if (expoente > 0) {
        val ponto = expoente
        while (expoente != 0) {
            binarioNormalizado.addLast('0')
            expoente--
        }
        binarioNormalizado.add(ponto, '.')
    } else {
        if(expoente < 0) {
            while (expoente != 0) {
                binarioNormalizado.addFirst('0')
                expoente++
            }
        }
        binarioNormalizado.addFirst('.')
        binarioNormalizado.addFirst('0')
    }

    return converteBinarioParaDecimal(binarioNormalizado.joinToString(""))
}

fun converteDecimalParaBinario(decimal: String): String {
    var ehNegativo = false
    if (decimal.contains("-")) {
        decimal.replace("-", "")
        ehNegativo = true
    }

    val partes = separaPartes(decimal)
    val inteiroBinario = converteInteiroParaBinario(partes.first.toLong()) + "."
    var binario = inteiroBinario // FIXME TA DANDO ERRO AQUItirar os zeros da direita
    // FIXME E se o número inteiro der exatamente o número de casas da precisão?
    //  Talvez só verificar se o fracionario é diferente de 0 e se a ultima casa do inteiro é 1

    if (inteiroBinario.length - 1 < precisao) {
        val fracionarioBinario = converteFracionarioParaBinario(partes.second.toDouble(), inteiroBinario.length-1)
        if (binario.isNotEmpty()) binario += fracionarioBinario else binario = "0.$fracionarioBinario" // FIXME forma de formatar ta feia
    }
    if (ehNegativo) {
        binario = aplicaComplementoDe2(binario)
    }
    return binario
}

fun converteBinarioInteiroParaDecimal(binInteiro: String): Long {
    val bits = binInteiro.split("").filterNot { it.isEmpty() }
    val bitsInvertidos = bits.reversed()
    var decimal: Long = 0
    for (i in bitsInvertidos.indices) {
        if (bitsInvertidos[i] == "1") {
            decimal += 2.0.pow(i).toLong()
        }
    }
    return decimal
}

fun converteBinarioFracionarioParaDecimal(binFracionario: String): Double {
    val bits = binFracionario
        .replace("0.", "")
        .split("")
        .filterNot { it.isEmpty() }

    var decimal = 0.0
    for (i in bits.indices) {
        /**
         * input = 0.33333
         * i = 52, decimal = 0.3333299999999999
         * i = 53, decimal = 0.33332999999999996
         * i = 54, decimal = 0.33333
         * */
        if (bits[i] == "1") {
            decimal += 2.0.pow(-(i+1))
        }
    }
    return decimal
}

fun converteBinarioParaDecimal(binario: String): String {
    val partes = separaPartes(binario)
    val decimalInteiro = converteBinarioInteiroParaDecimal(partes.first) // TODO Tenho que debugar isso
    val decimalFracionario = converteBinarioFracionarioParaDecimal(partes.second)
    return "${decimalInteiro + decimalFracionario}"
} // FIXME Talvez devesse retornar um decimal
