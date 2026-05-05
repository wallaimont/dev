#Include "protheus.ch"

User Function ZZInclTit()
    DbSelectArea("SE2")
    SE2->(DbSetOrder(1))

    If !SE2->(DbSeek(xFilial("SE2") + "000001" + "01" + "ABC" + "000123" + "01" + "NF"))
        RecLock("SE2", .T.)
        SE2->E2_FILIAL  := xFilial("SE2")
        SE2->E2_FORNECE := "000001"
        SE2->E2_LOJA    := "01"
        SE2->E2_PREFIXO := "ABC"
        SE2->E2_NUM     := "000123"
        SE2->E2_PARCELA := "01"
        SE2->E2_TIPO    := "NF"
        SE2->E2_VALOR   := 1500
        SE2->E2_SALDO   := 1500
        MsUnlock()
        MsgInfo("Título incluído.")
    Else
        MsgStop("Título já existe.")
    EndIf

Return

//Inclusão de título financeiro em SE2 Manipulação direta de financeiro, 
útil para sustentação e automações.
