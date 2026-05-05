#Include "protheus.ch"

User Function ZZCopiaTit()
    Local cNovoNum := "000124"

    DbSelectArea("SE2")
    SE2->(DbSetOrder(1))

    If SE2->(DbSeek(xFilial("SE2") + "000001" + "01" + "ABC" + "000123" + "01" + "NF"))
        RecLock("SE2", .T.)
        SE2->E2_FILIAL  := xFilial("SE2")
        SE2->E2_FORNECE := "000001"
        SE2->E2_LOJA    := "01"
        SE2->E2_PREFIXO := "ABC"
        SE2->E2_NUM     := cNovoNum
        SE2->E2_PARCELA := "01"
        SE2->E2_TIPO    := "NF"
        SE2->E2_VALOR   := 1500
        SE2->E2_SALDO   := 1500
        MsUnlock()
        MsgInfo("Título copiado com sucesso.")
    Else
        MsgStop("Título origem não encontrado.")
    EndIf

Return

//Cópia simples de título financeiro
copiaria muito mais campos e validaria duplicidade do novo número
