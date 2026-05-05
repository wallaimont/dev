#Include "protheus.ch"

User Function ZZValSaldo(cProduto, cLocal, nQtd)
    Local lRet := .F.

    DbSelectArea("SB2")
    SB2->(DbSetOrder(1))

    If SB2->(DbSeek(xFilial("SB2") + cProduto + cLocal))
        If SB2->B2_QATU >= nQtd
            lRet := .T.
        Else
            MsgStop("Saldo insuficiente para movimentação.")
        EndIf
    Else
        MsgStop("Saldo não encontrado para o produto/local.")
    EndIf

Return lRet

// Validação de saldo em SB2 antes de operação 
Impede operação logística/estoque sem saldo disponível
