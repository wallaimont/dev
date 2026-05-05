#Include "protheus.ch"

User Function ZZProdBloq()
    Local cProduto := Space(15)

    cProduto := AllTrim(FwInputBox("Informe o código do produto:"))

    If Empty(cProduto)
        MsgStop("Produto não informado.")
        Return
    EndIf

    DbSelectArea("SB1")
    SB1->(DbSetOrder(1)) // índice por código
    If SB1->(DbSeek(xFilial("SB1") + cProduto))
        RecLock("SB1", .F.)
        SB1->B1_MSBLQL := "1"
        MsUnlock()
        MsgInfo("Produto bloqueado com sucesso.")
    Else
        MsgStop("Produto não encontrado.")
    EndIf

Return

// customização via menu/ponto de entrada para bloquear produto no cadastro, 
usando busca na SB1, controle de lock com RecLock/MsUnlock e atualização 
segura do registro //


