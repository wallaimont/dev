#Include "protheus.ch"

User Function ZZValCliPed(cCliente, cLoja)
    Local lRet := .T.

    DbSelectArea("SA1")
    SA1->(DbSetOrder(1))

    If SA1->(DbSeek(xFilial("SA1") + cCliente + cLoja))
        If SA1->A1_MSBLQL == "1"
            MsgStop("Cliente bloqueado para operação.")
            lRet := .F.
        EndIf
    Else
        MsgStop("Cliente não encontrado.")
        lRet := .F.
    EndIf

Return lRet

//Validação de cliente bloqueado antes do pedido 
Regra de negócio no comercial antes de faturar ou vender
