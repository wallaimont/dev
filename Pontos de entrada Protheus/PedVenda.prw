#Include "protheus.ch"

/*/{Protheus.doc} ZZPedVenda
    Gera pedido de venda automático usando MATA410.
@type  Function
/*/
User Function ZZPedVenda()
    Local aCab   := {}
    Local aItens := {}

    Private lMsErroAuto    := .F.
    Private lMsHelpAuto    := .T.
    Private lAutoErrNoFile := .T.

    // Cabeçalho do pedido
    AAdd(aCab, {"C5_CLIENTE", "000500", Nil})
    AAdd(aCab, {"C5_LOJACLI", "01", Nil})
    AAdd(aCab, {"C5_TIPO"   , "N", Nil})
    AAdd(aCab, {"C5_CONDPAG", "001", Nil})

    // Itens do pedido
    AAdd(aItens, {;
        {"C6_PRODUTO", "PROD2001", Nil},;
        {"C6_QTDVEN" , 3, Nil},;
        {"C6_PRCVEN" , 150.00, Nil} })

    // Executa a rotina automática
    MSExecAuto({|x,y,z| MATA410(x,y,z)}, aCab, aItens, 3)

    If lMsErroAuto
        MostraErro()
    Else
        MsgInfo("Pedido de venda gerado com sucesso.")
    EndIf

Return

// Pedido de venda via ExecAuto no MATA410
