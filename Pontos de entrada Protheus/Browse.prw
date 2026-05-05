#Include "protheus.ch"

User Function ZZBrowse()
    Local aDados := {}
    Local cAlias := "SA1"

    DbSelectArea(cAlias)
    (cAlias)->(DbSetOrder(1))
    (cAlias)->(DbGoTop())

    While !(cAlias)->(Eof())
        AAdd(aDados, { ;
            (cAlias)->A1_COD, ;
            (cAlias)->A1_LOJA, ;
            AllTrim((cAlias)->A1_NOME) ;
        })
        (cAlias)->(DbSkip())
    EndDo

    aBrowse(aDados, {"Código", "Loja", "Nome"}, "Clientes")

Return

// browse simples consultas internas
