#Include "protheus.ch"
#Include "topconn.ch"

/*/{Protheus.doc} ZZTotVendaCli
    Calcula total vendido por cliente.
@type  Function
/*/
User Function ZZTotVendaCli()
    Local cAlias   := GetNextAlias()
    Local cCliente := "000500"
    Local cQuery   := ""
    Local nTotal   := 0

    cQuery := " SELECT SUM(C6_VALOR) TOTAL " + ;
              " FROM " + RetSqlName("SC6") + " SC6 " + ;
              " INNER JOIN " + RetSqlName("SC5") + " SC5 " + ;
              "   ON SC5.C5_FILIAL = SC6.C6_FILIAL " + ;
              "  AND SC5.C5_NUM    = SC6.C6_NUM " + ;
              "  AND SC5.D_E_L_E_T_ = ' ' " + ;
              " WHERE SC6.D_E_L_E_T_ = ' ' " + ;
              "   AND SC5.C5_CLIENTE = '" + cCliente + "' "

    DbUseArea(.T., "TOPCONN", TcGenQry(,,cQuery), cAlias, .T., .T.)

    If !(cAlias)->(Eof())
        nTotal := (cAlias)->TOTAL
    EndIf

    (cAlias)->(DbCloseArea())

    MsgInfo("Total vendido: " + Transform(nTotal, "@E 999,999,999.99"))

Return

//Query para total vendido por cliente
