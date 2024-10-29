var sinal: String = ""
var precisao: Int = 0
var lower: Int = 0
var upper: Int = 0

/**
 * Limpa a tela movendo o cursor para o canto superior esquerdo e limpando a tela
 * através de sequências de escape ANSI, além de limpar o buffer de saída do sistema.
 */
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
    var binarioProcessado = binario.replace("...", "")
    while (binarioProcessado.length-1 < precisao) binarioProcessado += "0"

    var expoente = binarioProcessado.indexOf('.') // Pega quantas casas há antes do ponto, útil para números com casas inteiras 100.1 -> expoente = 3 -> 0.1001 * 2^3

    val binLista = ("." + binarioProcessado.replace(".", ""))
        .split("").filterNot { it.isEmpty() }
        .toMutableList() // Retira o ponto e adiciona "." ao início. Usa filter para retirar "" que aparece no começo e final da lista
    // contains 1 por causa do zero
    while (binLista[1] == "0" && binLista.contains("1")) { // Retirar os 0s após os o ponto para a casa após o ponto ficar com o valor 1 da normalização
        binLista.removeAt(1)
        expoente-- // Diminui o expoente para cada 0 perdido
    }// 0.001 -> 0.1 * 2^(-2)

    if (expoente > upper) {
        println("Houve um Overflow!")
    } else if (expoente < lower) {
        println("Houve um Underflow")
    }

    var mantissa = binLista.joinToString("")
    if (binLista.size-1 > precisao) {
        mantissa = mantissa.substring(0, precisao+1)
        println("Houve um truncamento.")
    }
    val binarioNormalizado = "${mantissa}*2^($expoente)"

    return binarioNormalizado
}

fun main() {
    print(
        """
    Seja Bem vindo ao emulador de Maquinas Finitas!
    Digite primeiramente o valor da precisão: """
    )
    precisao = readln().toInt()
    apagaTela()

    do {
        print("Digite o valor do expoente de menor valor l (lower): ")
        lower = readln().toInt()
        apagaTela()

        print("Digite o valor do expoente de maior valor u (upper): ")
        upper = readln().toInt()
        apagaTela()

        if (lower >  upper)
            println("O valor de lower não pode ser maior que o de upper, digite novamente os valores!")
    } while (lower > upper)

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
            println("Binário encontrado: $binario")

            val binarioNormalizado = normalizaNumero(binario)
            println("Número normalizado armazenado em binário: $binarioNormalizado")
            val bitDeSinal = if (sinal == "-") "1" else "0"
            println("Em S/A: $bitDeSinal $binarioNormalizado")

            val binarioEmComplementoDe1 = if (sinal.contains("-")) aplicaComplementoDe1(binarioNormalizado) else binarioNormalizado
            println("Em complemento de 1: $bitDeSinal $binarioEmComplementoDe1")

            val binarioEmComplementoDe2 = if (sinal.contains("-")) aplicaComplementoDe2(binarioNormalizado) else binarioNormalizado
            println("Em complemento de 2: $bitDeSinal $binarioEmComplementoDe2")

            println("Valor binario normalizado convertido para decimal: ${sinal + converteBinarioNormalizadoParaDecimal(binarioEmComplementoDe2)}")
        } else break
    } while (true)
}