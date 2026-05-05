#Include "protheus.ch"

/*/{Protheus.doc} ZZInclTit
    Inclui título financeiro em SE2.
@type  Function
/*/
User Function ZZInclTit()
    Local cFornec  := "000001"
    Local cLoja    := "01"
    Local cPrefixo := "NF1"
    Local cNumero  := "000900"
    Local cParcela := "01"
    Local cTipo    := "NF"

    DbSelectArea("SE2")
    SE2->(DbSetOrder(1))

    // Só inclui se ainda não existir
    If !SE2->(DbSeek(xFilial("SE2") + cFornec + cLoja + cPrefixo + cNumero + cParcela + cTipo))

        RecLock("SE2", .T.)
        SE2->E2_FILIAL  := xFilial("SE2")
        SE2->E2_FORNECE := cFornec
        SE2->E2_LOJA    := cLoja
        SE2->E2_PREFIXO := cPrefixo
        SE2->E2_NUM     := cNumero
        SE2->E2_PARCELA := cParcela
        SE2->E2_TIPO    := cTipo
        SE2->E2_VALOR   := 2500
        SE2->E2_SALDO   := 2500
        MsUnlock()

        MsgInfo("Título incluído com sucesso.")
    Else
        MsgStop("Título já existe.")
    EndIf

Return

//Inclusão direta de título financeiro
