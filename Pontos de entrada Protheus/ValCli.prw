#Include "protheus.ch"

/*/{Protheus.doc} ZZValCli
    Valida se o cliente está bloqueado antes de seguir na operação.
@type  Function
/*/
User Function ZZValCli(cCliente, cLoja)
    Local lRet := .T.

    DbSelectArea("SA1")
    SA1->(DbSetOrder(1))

    // Procura cliente na SA1
    If SA1->(DbSeek(xFilial("SA1") + cCliente + cLoja))

        // Verifica flag de bloqueio
        If SA1->A1_MSBLQL == "1"
            MsgStop("Cliente bloqueado para operação.")
            lRet := .F.
        EndIf
    Else
        MsgStop("Cliente não encontrado.")
        lRet := .F.
    EndIf

Return lRet

//Validação de cliente bloqueado
