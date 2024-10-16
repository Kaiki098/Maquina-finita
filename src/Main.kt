import java.util.*
import kotlin.math.pow

var precisao: Int = 0
var lower: Int = 0
var upper: Int = 0

fun apagaTela() {
    print("\u001b[H\u001b[2J")
    System.out.flush()
}

fun separaPartes(numero: String): Pair<String, String> {
    return try {
        val numeroQuebrado = numero.split('.')
        Pair(numeroQuebrado[0], "0.${numeroQuebrado[1]}")
    } catch (e: Exception) {
        println("Não foi possível separar as partes devido ao erro: ${e.message}")
        Pair("", "")
    }
}

fun normalizaNumero(binario: String): String {
    var expoente = binario.indexOf('.')
    val bin = ("0." + binario.replace(".", "")).split("").toMutableList()
    while (bin[3] == "0"){
        bin.removeAt(3)
        expoente--
    }

    if (expoente >= upper) { // FIXME Precisa disso?
        expoente = upper
        println("Houve um Overflow!")
    } else if (expoente <= lower) {
        expoente = lower
        println("Houve um Underflow")
    }

    return "${bin.joinToString("")}*2^($expoente)"
}

/**
 * Se preocupar com o zero positivo e negativo, números negativos e números racionais e irracionais do demônio 0.666
 */

fun main() {
    print("""
    Seja Bem vindo ao emulador de Maquinas Finitas!
    Digite primeiramente o valor da precisão: """)
    precisao = readln().toInt()
    apagaTela()

    print("Digite o valor do expoente de menor valor l (lower): ")
    lower = readln().toInt()
    apagaTela()

    print("Digite o valor do expoente de maior valor u (upper): ")
    upper = readln().toInt()
    apagaTela()

    println("Precisao: $precisao, lower: $lower, upper: $upper")

    val min = (1 / (10 * precisao)) * 2.0.pow(lower)
    val max = 0.1 * 10 * precisao * 2.0.pow(upper) // FIXME rever isso

    println("Valor decimal mínimo: $min")
    println("Valor decimal máximo: $max")
    do {
        println("Digite um valor: ")
        val valor = readln() // FIXME Quando digito sem ponto da pau

        println("Valor decimal: $valor")

        val binario = converteDecimalParaBinario(valor)
        println("Valor binario: $binario")
        println("Valor binario normatizado: ${normalizaNumero(binario)}")

        println("Valor bin convertido para decimal: ${converteBinarioParaDecimal(binario)}") // FIXME Necessário desnormalizar primeiro
    } while (false);

}