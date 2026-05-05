#Include "protheus.ch"

User Function ZZValDup()
    Local cFornec  := "000001"
    Local cLoja    := "01"
    Local cPrefixo := "ABC"
    Local cNumero  := "000123"
    Local cParcela := "01"
    Local cTipo    := "NF"

    DbSelectArea("SE2")
    SE2->(DbSetOrder(1))

    If SE2->(DbSeek(xFilial("SE2") + cFornec + cLoja + cPrefixo + cNumero + cParcela + cTipo))
        MsgStop("Título já cadastrado.")
        Return .T.
    EndIf

Return .F.

// Validação de duplicidade de título em SE2 
Implementei prevenção de duplicidade no financeiro para evitar 
inconsistência contábil e retrabalho
