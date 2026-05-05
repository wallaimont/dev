#Include "protheus.ch"

/*/{Protheus.doc} ZZAltLocal
    Exemplo didático de alteração de local no estoque.
@type  Function
/*/
User Function ZZAltLocal()
    Local cProduto := "PROD2001"
    Local cLocalDe := "01"
    Local cLocalPa := "02"

    DbSelectArea("SB2")
    SB2->(DbSetOrder(1))

    // Procura saldo no local de origem
    If SB2->(DbSeek(xFilial("SB2") + cProduto + cLocalDe))

        // Altera local
        RecLock("SB2", .F.)
        SB2->B2_LOCAL := cLocalPa
        MsUnlock()

        MsgInfo("Local do produto alterado com sucesso.")
    Else
        MsgStop("Saldo não encontrado.")
    EndIf

Return

//Alteração de local de estoque
