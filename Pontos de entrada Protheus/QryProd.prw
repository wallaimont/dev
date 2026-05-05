#Include "protheus.ch"
#Include "topconn.ch"

/*/{Protheus.doc} ZZQryProd
    Consulta produtos ativos usando SQL com TopConn.
@type  Function
/*/
User Function ZZQryProd()
    Local cAlias := GetNextAlias()
    Local cQuery := ""

    // Monta SQL usando nome físico da tabela
    cQuery := " SELECT B1_COD, B1_DESC, B1_UM " + ;
              " FROM " + RetSqlName("SB1") + " SB1 " + ;
              " WHERE SB1.D_E_L_E_T_ = ' ' " + ;
              "   AND SB1.B1_FILIAL = '" + xFilial("SB1") + "' "

    // Abre resultado da query em alias temporário
    DbUseArea(.T., "TOPCONN", TcGenQry(,,cQuery), cAlias, .T., .T.)

    // Percorre os registros retornados
    While !(cAlias)->(Eof())
        ConOut((cAlias)->B1_COD + " - " + (cAlias)->B1_DESC)
        (cAlias)->(DbSkip())
    EndDo

    // Fecha alias
    (cAlias)->(DbCloseArea())

Return

//Consulta SQL de produtos com TopConn 
Em cenários de relatório ou análise, costumo usar TopConn com 
RetSqlName e filtro de D_E_L_E_T_ para manter consistência
