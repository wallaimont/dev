#Include "protheus.ch"

/*/{Protheus.doc} ZZInclProd
    Exemplo de inclusão automática de produto usando ExecAuto.
@type  Function
/*/
User Function ZZInclProd()
    Local aCab := {}

    // Variáveis de controle do ExecAuto
    Private lMsErroAuto    := .F.
    Private lMsHelpAuto    := .T.
    Private lAutoErrNoFile := .T.

    // Monta estrutura com os campos do cadastro
    AAdd(aCab, {"B1_COD"  , "PROD2001", Nil})
    AAdd(aCab, {"B1_DESC" , "PRODUTO GERADO VIA EXECAUTO", Nil})
    AAdd(aCab, {"B1_TIPO" , "PA", Nil})
    AAdd(aCab, {"B1_UM"   , "UN", Nil})
    AAdd(aCab, {"B1_GRUPO", "001", Nil})

    // Executa rotina padrão Mata010 de forma automática
    MSExecAuto({|a,b| Mata010(a,b)}, aCab, 3)

    // Verifica se houve erro
    If lMsErroAuto
        MostraErro()
    Else
        MsgInfo("Produto incluído com sucesso.")
    EndIf

Return

//Inclusão de produto via ExecAuto Esse padrão é muito útil para 
integração e carga automatizada, porque reutiliza a regra da rotina padrão
