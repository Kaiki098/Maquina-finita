import java.util.*

var precisao: Int? = null
var lower: Int? = null
var upper: Int? = null

fun apagaTela() {
    print("\u001b[H\u001b[2J")
    System.out.flush()
}

fun retiraParteInteira(decimal: String) =
    "0.${decimal.split('.')[1]}"


fun retiraParteFracionaria(decimal: String) =
    decimal.split('.')[0]


fun separaPartes(decimal: String): Pair<String, String> {
    val parteInteira = retiraParteFracionaria(decimal)
    val parteFracionaria = retiraParteInteira(decimal)

    return Pair(parteInteira, parteFracionaria)
}

fun converteInteiroParaBinario(inteiro: Long): BitSet {
    var aux = inteiro
    val pilha = Stack<String>()
    while (aux > 0) {
        pilha.push((aux % 2).toString())
        aux /= 2
    }
    val binario =  BitSet(100)
    var i = 0
    while (pilha.isNotEmpty())  {
        if (pilha.pop()[0] == '1') binario.set(i)
        i++
    }

    return binario
}

fun converteFracionarioParaBinario(fracionario: Double): BitSet {
    val conjuntoBinario = BitSet(100)
    var aux = fracionario
    for(i in 0..<conjuntoBinario.size()) {
        aux *= 2
        if (aux > 1) {
            conjuntoBinario.set(i)
            aux = retiraParteInteira(aux.toString()).toDouble()
        }
    }

    return conjuntoBinario
}

fun bitSetToString(bitSet: BitSet): String {
    val sb = StringBuilder()
    for (i in 0..<bitSet.length()) {
        sb.append(if (bitSet[i]) '1' else '0')
    }
    return sb.toString()
}


fun converteDecimalParaBinario(decimal: String): String {
    val partes = separaPartes(decimal)

    val inteiroBinario = converteInteiroParaBinario(partes.first.toLong())
    val fracionarioBinario = converteFracionarioParaBinario(partes.second.toDouble())
    val inteiroBinarioString = bitSetToString(inteiroBinario)
    val fracionarioBinarioString = bitSetToString(fracionarioBinario)

    return "$inteiroBinarioString.$fracionarioBinarioString"
}

fun converteBinarioParaDecimal() {

}

fun normalizaNumero(binario: String) {
    val ponto = binario.indexOf('.')
    val binarioNormalizado = binario.replace(".", "").substring(0, precisao!!)
    if (binarioNormalizado.last() == '1') {

    }
}

fun main() {
    print("""
    Seja Bem vindo ao emulador de Maquinas Finitas!
    Digite primeiramente o valor da precisão: """)
    precisao = readlnOrNull()?.toInt() // TODO tirar esse null
    apagaTela()

    print("Digite o valor do expoente de menor valor l (lower): ")
    lower = readlnOrNull()?.toInt()
    apagaTela()

    print("Digite o valor do expoente de maior valor u (upper): ")
    upper = readlnOrNull()?.toInt()
    apagaTela()

    println("Precisao: $precisao, lower: $lower, upper: $upper")

    do {
        println("Digite um valor: ")
        val valor = readln()

        println("Valor decimal: $valor")

        val binario = converteDecimalParaBinario(valor)
        println("Valor binario: $binario")

        println("Valor binario normatizado: ")

    } while (false);

}