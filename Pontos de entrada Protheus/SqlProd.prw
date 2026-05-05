#Include "protheus.ch"
#Include "topconn.ch"

User Function ZZSqlProd()
    Local cAlias := GetNextAlias()
    Local cQuery := ""

    cQuery := " SELECT B1_COD, B1_DESC, B1_TIPO " + ;
              " FROM " + RetSqlName("SB1") + " SB1 " + ;
              " WHERE SB1.D_E_L_E_T_ = ' ' " + ;
              "   AND B1_FILIAL = '" + xFilial("SB1") + "' "

    DbUseArea(.T., "TOPCONN", TcGenQry(,,cQuery), cAlias, .T., .T.)

    While !(cAlias)->(Eof())
        ConOut((cAlias)->B1_COD + " - " + (cAlias)->B1_DESC)
        (cAlias)->(DbSkip())
    EndDo

    (cAlias)->(DbCloseArea())

Return

// Consulta SQL de produtos ativos 
Lista produtos ativos por SQL usando TopConn
SQL via TopConn para relatórios e consultas críticas, sempre filtrando D_E_L_E_T_ 
e usando RetSqlName para respeitar a estrutura do banco
