#Include "protheus.ch"

/*/{Protheus.doc} ZZQtdSku
    Conta SKUs distintos a partir dos itens do pedido.
@type  Function
/*/
User Function ZZQtdSku()
    Local aSku  := {}
    Local cProd := ""

    DbSelectArea("SC6")
    SC6->(DbSetOrder(1))
    SC6->(DbGoTop())

    // Percorre itens
    While !SC6->(Eof())
        cProd := AllTrim(SC6->C6_PRODUTO)

        // Se ainda nÃ£o existe no array, adiciona
        If AScan(aSku, cProd) == 0
            AAdd(aSku, cProd)
        EndIf

        SC6->(DbSkip())
    EndDo

    MsgInfo("Quantidade de SKU distintos: " + CValToChar(Len(aSku)))

Return

//CÃ¡lculo de quantidade de SKU Lógica para contar SKUs distintos, 
útil em logística e separação
