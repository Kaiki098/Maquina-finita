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
    val bin = ("0." + binario.replace(".", ""))
        .split("").filterNot { it.isEmpty() }
        .toMutableList() // Retira o ponto e adiciona "0." ao início. Usa filter para retirar "" que aparece no começo e final da lista

    while (bin[2] == "0") { // Retirar os 0s após os o ponto para a casa após o ponto ficar com o valor 1 da normalização
        bin.removeAt(2)
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
    val testeSoma = mutableListOf('1', '1', '1', '1', '1', '1')
    somaBit(testeSoma, testeSoma.size - 1)
    println(testeSoma)
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

    val binNormalizadoMin = "0.1*2^($lower)"
    var binMax = "0."
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
            println("Valor binario: $binario")

            val binarioNormalizado = normalizaNumero(binario)
            val bitDeSinal = if (sinal == "-") "1" else "0"

            println("Número normalizado armazenado em binário: $bitDeSinal $binarioNormalizado")
            println("Valor bin convertido para decimal: ${sinal + converteBinarioNormalizadoParaDecimal(binarioNormalizado)}")
        } else break
    } while (true);
}