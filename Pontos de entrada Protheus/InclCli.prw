#Include "protheus.ch"

User Function ZZInclCli()
    Local cCod := "000123"
    Local cLoja := "01"

    DbSelectArea("SA1")
    SA1->(DbSetOrder(1))

    If !SA1->(DbSeek(xFilial("SA1") + cCod + cLoja))
        RecLock("SA1", .T.)
        SA1->A1_FILIAL := xFilial("SA1")
        SA1->A1_COD    := cCod
        SA1->A1_LOJA   := cLoja
        SA1->A1_NOME   := "CLIENTE TESTE ADVPL"
        SA1->A1_NREDUZ := "CLIENTE TESTE"
        SA1->A1_TIPO   := "F"
        SA1->A1_PESSOA := "J"
        MsUnlock()
        MsgInfo("Cliente incluído com sucesso.")
    Else
        MsgStop("Cliente já existe.")
    EndIf

Return


// manipulação de dados no padrão Protheus
