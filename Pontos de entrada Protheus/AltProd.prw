#Include "protheus.ch"

/*/{Protheus.doc} ZZAltProd
    Altera descrição reduzida de um produto existente.
@type  Function
/*/
User Function ZZAltProd()
    Local cProduto := "PROD2001"

    DbSelectArea("SB1")
    SB1->(DbSetOrder(1)) // ordem por filial + código

    // Localiza o produto
    If SB1->(DbSeek(xFilial("SB1") + cProduto))

        // Trava o registro para alteração
        RecLock("SB1", .F.)
        SB1->B1_DESC := "PRODUTO ALTERADO EM ROTINA CUSTOM"
        MsUnlock()

        MsgInfo("Produto alterado com sucesso.")
    Else
        MsgStop("Produto não encontrado.")
    EndIf

Return

// Alteração segura de produto na SB1 alteração segura de cadastro com 
RecLock/MsUnlock, padrão importante em ADVPL
