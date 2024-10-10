fun apagaTela() {
    // TODO
}

var precisao: Int? = null
var lower: Int? = null
var upper: Int? = null

fun main() {
    print("""
    Seja Bem vindo ao emulador de Maquinas Finitas!
    Digite primeiramente o valor da precisâo: """)
    precisao = readlnOrNull()?.toInt()
    apagaTela()
    print("Digite o valor do expoente de menor valor l (lower): ")
    lower = readlnOrNull()?.toInt()
    apagaTela()
    print("Digite o valor do expoente de maior valor u (upper): ")
    upper = readlnOrNull()?.toInt()
    apagaTela()

    println("Precisao: $precisao, lower: $lower, upper: $upper")
}