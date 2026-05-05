#Include "protheus.ch"

User Function ZZPedido()
    Local aCab   := {}
    Local aItens := {}

    Private lMsErroAuto    := .F.
    Private lMsHelpAuto    := .T.
    Private lAutoErrNoFile := .T.

    AAdd(aCab, {"C5_CLIENTE", "000001", Nil})
    AAdd(aCab, {"C5_LOJACLI", "01", Nil})
    AAdd(aCab, {"C5_TIPO"   , "N", Nil})
    AAdd(aCab, {"C5_CONDPAG", "001", Nil})

    AAdd(aItens, {;
        {"C6_PRODUTO", "PROD1001", Nil},;
        {"C6_QTDVEN" , 5, Nil},;
        {"C6_PRCVEN" , 25.50, Nil} })

    MSExecAuto({|x,y,z| MATA410(x,y,z)}, aCab, aItens, 3)

    If lMsErroAuto
        MostraErro()
    Else
        MsgInfo("Pedido gerado com sucesso.")
    EndIf

Return

//Inclusão de pedido via ExecAuto no MATA410 ExecAuto no MATA410 
para automação de pedidos, reduzindo digitação manual e padronizando 
integração entre sistemas
