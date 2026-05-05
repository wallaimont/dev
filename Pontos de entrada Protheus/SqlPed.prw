#Include "protheus.ch"
#Include "topconn.ch"

User Function ZZSqlPed()
    Local cQuery := ""
    Local cAlias := GetNextAlias()

    cQuery := " SELECT C5_NUM, C5_CLIENTE, C5_EMISSAO " + ;
              " FROM " + RetSqlName("SC5") + " SC5 " + ;
              " WHERE SC5.D_E_L_E_T_ = ' ' " + ;
              "   AND C5_FILIAL = '" + xFilial("SC5") + "' "

    DbUseArea(.T., "TOPCONN", TcGenQry(,,cQuery), cAlias, .T., .T.)

    While !(cAlias)->(Eof())
        ConOut("Pedido: " + (cAlias)->C5_NUM + ;
               " Cliente: " + (cAlias)->C5_CLIENTE)
        (cAlias)->(DbSkip())
    EndDo

    (cAlias)->(DbCloseArea())

Return


// usa TopConn e TcGenQry para consultar pedidos diretamente no banco, 
respeitando a tabela física via RetSqlName e filtrando registros 
não deletados logicamente //
