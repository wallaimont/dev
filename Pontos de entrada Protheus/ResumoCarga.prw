#Include "protheus.ch"

/*/{Protheus.doc} ZZResumoCarga
    Exemplo simples de totalização de pedidos em carga.
@type  Function
/*/
User Function ZZResumoCarga()
    Local nPedidos := 0

    DbSelectArea("SC9")
    SC9->(DbSetOrder(1))
    SC9->(DbGoTop())

    // Conta registros encontrados
    While !SC9->(Eof())
        nPedidos++
        SC9->(DbSkip())
    EndDo

    MsgInfo("Total de pedidos encontrados: " + CValToChar(nPedidos))

Return

//Resumo simples de carga
