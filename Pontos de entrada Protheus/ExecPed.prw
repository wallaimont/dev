#Include "protheus.ch"

User Function ZZExecPed()
    Local aCab := {}
    Local aItens := {}
    Local lMsErroAuto := .F.

    Private lMsHelpAuto := .T.
    Private lAutoErrNoFile := .T.

    AAdd(aCab, {"C5_CLIENTE", "000001", Nil})
    AAdd(aCab, {"C5_LOJACLI", "01", Nil})
    AAdd(aCab, {"C5_CONDPAG", "001", Nil})
    AAdd(aCab, {"C5_TIPO", "N", Nil})

    AAdd(aItens, { ;
        {"C6_PRODUTO", "000000000000001", Nil}, ;
        {"C6_QTDVEN", 1, Nil}, ;
        {"C6_PRCVEN", 100, Nil} ;
    })

    MSExecAuto({|x,y,z| MATA410(x,y,z)}, aCab, aItens, 3)

    If lMsErroAuto
        MostraErro()
    Else
        MsgInfo("Pedido gerado com sucesso.")
    EndIf

Return

// automatiza uma rotina padrão com ExecAuto, montando arrays
de cabeçalho e itens e executando o processo sem interação manual do usuário
