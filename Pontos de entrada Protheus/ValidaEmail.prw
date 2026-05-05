#Include "protheus.ch"

User Function ZZValidaEmail(cEmail)
    Local lRet := .T.

    If Empty(AllTrim(cEmail))
        MsgStop("E-mail não informado.")
        lRet := .F.
    ElseIf At("@", cEmail) == 0
        MsgStop("E-mail inválido.")
        lRet := .F.
    EndIf

Return lRet

// validação antes de gravar
