#Include "protheus.ch"

/*/{Protheus.doc} ZZInclCli
    Inclui cliente se não existir.
@type  Function
/*/
User Function ZZInclCli()
    Local cCod  := "000500"
    Local cLoja := "01"

    DbSelectArea("SA1")
    SA1->(DbSetOrder(1)) // filial + código + loja

    // Verifica se cliente já existe
    If !SA1->(DbSeek(xFilial("SA1") + cCod + cLoja))

        // Abre novo registro para inclusão
        RecLock("SA1", .T.)
        SA1->A1_FILIAL := xFilial("SA1")
        SA1->A1_COD    := cCod
        SA1->A1_LOJA   := cLoja
        SA1->A1_NOME   := "CLIENTE ENTREVISTA ADVPL"
        SA1->A1_NREDUZ := "CLIENTE ADVPL"
        SA1->A1_TIPO   := "F"
        SA1->A1_PESSOA := "J"
        MsUnlock()

        MsgInfo("Cliente incluído com sucesso.")
    Else
        MsgStop("Cliente já cadastrado.")
    EndIf

Return

//Inclusão de cliente na SA1
