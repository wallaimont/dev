#Include "protheus.ch"

/*/{Protheus.doc} F050ROT
    Adiciona opção customizada no financeiro.
@type  Function
/*/
User Function F050ROT()
    Local aRotina := {}

    // Adiciona opção de cópia de título
    AAdd(aRotina, {"Copiar Título", "U_ZZCopiaTit()", 0, 2, 0, Nil})

Return aRotina

// Ponto de entrada no FINA050 para copiar título
ponto de entrada no FINA050 para agregar a��es espec�ficas de neg�cio no contas a pagar
