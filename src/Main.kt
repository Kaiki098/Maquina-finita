var sinal: String = ""
var precisao: Int = 0
var lower: Int = 0
var upper: Int = 0

fun apagaTela() {
    print("\u001b[H\u001b[2J")
    System.out.flush()
}

/**
 * Normaliza o número binário definindo primeiramente o expoente com valor da posição do ponto,
 * retira o ponto e remove os 0s após o ponto até que seja encontrado o número 1 e a cada remoção
 * diminui o valor do expoente. Avisa se ocorreu um overflow ou underflow e retorna o binário
 * normalizado.
 */
fun normalizaNumero(binario: String): String {
    var expoente = binario.indexOf('.') // Pega quantas casas há antes do ponto, útil para números com casas inteiras 100.1 -> expoente = 3 -> 0.1001 * 2^3
    val bin = ("." + binario.replace(".", ""))
        .split("").filterNot { it.isEmpty() }
        .toMutableList() // Retira o ponto e adiciona "." ao início. Usa filter para retirar "" que aparece no começo e final da lista
    // contains 1 por causa do zero
    while (bin[1] == "0" && bin.contains("1")) { // Retirar os 0s após os o ponto para a casa após o ponto ficar com o valor 1 da normalização
        bin.removeAt(1)
        expoente-- // Diminui o expoente para cada 0 perdido
    }// 0.001 -> 0.1 * 2^(-2)

    if (expoente > upper) {
        println("Houve um Overflow!")
    } else if (expoente < lower) {
        println("Houve um Underflow")
    }

    return "${bin.joinToString("")}*2^($expoente)"
}


/**
 * Se preocupar com o zero positivo e negativo, números negativos e números racionais e irracionais do demônio 0.666
 */

/**
 * FIXME Ainda com problema no número -163.63 para 8 de precisão, l = -5, u = 5
 */

fun main() {
    println(converteBinarioNormalizadoParaDecimal(".101*2^(2)"))

    print(
        """
    Seja Bem vindo ao emulador de Maquinas Finitas!
    Digite primeiramente o valor da precisão: """
    )
    precisao = readln().toInt()
    apagaTela()

    print("Digite o valor do expoente de menor valor l (lower): ")
    lower = readln().toInt()
    apagaTela()

    print("Digite o valor do expoente de maior valor u (upper): ")
    upper = readln().toInt()
    apagaTela()

    println("Precisao: $precisao, lower: $lower, upper: $upper")

    val binNormalizadoMin = ".1*2^($lower)"
    var binMax = "."
    repeat(precisao) {
        binMax += "1"
    }
    val min = converteBinarioNormalizadoParaDecimal(binNormalizadoMin)
    val max = converteBinarioNormalizadoParaDecimal("$binMax*2^($upper)")
    println("Valor decimal mínimo: $min")
    println("Valor decimal máximo: $max")

    do {
        println("Digite um valor ou \"sair\": ")
        val valor = readln()

        if (valor != "sair") {
            println("Valor decimal: $valor")

            val binario = converteDecimalParaBinario(valor)
            val binarioNormalizado = normalizaNumero(binario)
            val binarioEmComplementoDe1 = if (sinal.contains("-")) aplicaComplementoDe1(binarioNormalizado) else binarioNormalizado
            val binarioEmComplementoDe2 = if (sinal.contains("-")) aplicaComplementoDe2(binarioNormalizado) else binarioNormalizado
            val bitDeSinal = if (sinal == "-") "1" else "0"

            println("Binário encontrado: $binario")
            println("Número normalizado armazenado em binário: $binarioNormalizado")
            println("Em S/A: $bitDeSinal $binarioNormalizado")
            println("Em complemento de 1: $bitDeSinal $binarioEmComplementoDe1")
            println("Em complemento de 2: $bitDeSinal $binarioEmComplementoDe2")

            println("Valor binario normalizado convertido para decimal: ${sinal + converteBinarioNormalizadoParaDecimal(binarioEmComplementoDe2)}")
        } else break
    } while (true);
}