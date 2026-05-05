#Include "protheus.ch"

/*/{Protheus.doc} ZZCopiaTit
    Faz cópia simples de um título existente.
@type  Function
/*/
User Function ZZCopiaTit()
    Local cNovoNum := "000901"

    DbSelectArea("SE2")
    SE2->(DbSetOrder(1))

    // Procura título origem
    If SE2->(DbSeek(xFilial("SE2") + "000001" + "01" + "NF1" + "000900" + "01" + "NF"))

        // Inclui novo registro com outro número
        RecLock("SE2", .T.)
        SE2->E2_FILIAL  := xFilial("SE2")
        SE2->E2_FORNECE := "000001"
        SE2->E2_LOJA    := "01"
        SE2->E2_PREFIXO := "NF1"
        SE2->E2_NUM     := cNovoNum
        SE2->E2_PARCELA := "01"
        SE2->E2_TIPO    := "NF"
        SE2->E2_VALOR   := 2500
        SE2->E2_SALDO   := 2500
        MsUnlock()

        MsgInfo("Título copiado com sucesso.")
    Else
        MsgStop("Título origem não encontrado.")
    EndIf

Return

//Cópia simples de título financeiro
