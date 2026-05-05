#Include "protheus.ch"

/*/{Protheus.doc} ZZValDupTit
    Valida se o título já existe antes de incluir.
@type  Function
/*/
User Function ZZValDupTit(cFornec, cLoja, cPrefixo, cNumero, cParcela, cTipo)
    Local lExiste := .F.

    DbSelectArea("SE2")
    SE2->(DbSetOrder(1))

    // Busca pela chave composta do título
    If SE2->(DbSeek(xFilial("SE2") + cFornec + cLoja + cPrefixo + cNumero + cParcela + cTipo))
        lExiste := .T.
        MsgStop("Título já cadastrado.")
    EndIf

Return lExiste

// Validação de duplicidade de título em SE2
