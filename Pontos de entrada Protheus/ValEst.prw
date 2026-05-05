#Include "protheus.ch"

/*/{Protheus.doc} ZZValEst
    Valida se existe saldo suficiente no estoque.
@type  Function
/*/
User Function ZZValEst(cProduto, cLocal, nQtd)
    Local lRet := .F.

    DbSelectArea("SB2")
    SB2->(DbSetOrder(1)) // filial + produto + local

    // Localiza saldo
    If SB2->(DbSeek(xFilial("SB2") + cProduto + cLocal))

        // Compara saldo atual com quantidade solicitada
        If SB2->B2_QATU >= nQtd
            lRet := .T.
        Else
            MsgStop("Saldo insuficiente.")
        EndIf
    Else
        MsgStop("Produto/local não encontrado.")
    EndIf

Return lRet

//Validação de saldo em estoque na SB2
