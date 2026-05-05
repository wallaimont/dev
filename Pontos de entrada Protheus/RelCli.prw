#Include "protheus.ch"

User Function ZZRelCli()
    Local cTexto := ""

    DbSelectArea("SA1")
    SA1->(DbSetOrder(1))
    SA1->(DbGoTop())

    While !SA1->(Eof())
        cTexto += SA1->A1_COD + " - " + AllTrim(SA1->A1_NOME) + CRLF
        SA1->(DbSkip())
    EndDo

    MemoWrite("C:\TEMP\REL_CLIENTES.TXT", cTexto)
    MsgInfo("Relatório gerado com sucesso.")

Return

// relatório simples lógica de listagem e impressão de dados
