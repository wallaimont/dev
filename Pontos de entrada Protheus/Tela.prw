#Include "protheus.ch"

User Function ZZTela()
    Local oDlg
    Local oSay1
    Local oGet1
    Local oBtn1
    Local cNome := Space(30)

    DEFINE MSDIALOG oDlg TITLE "Tela ADVPL" FROM 000,000 TO 200,400 PIXEL

    @ 20, 20 SAY oSay1 PROMPT "Nome:" SIZE 40,10 OF oDlg PIXEL
    @ 20, 70 GET oGet1 VAR cNome SIZE 150,12 OF oDlg PIXEL

    @ 50, 20 BUTTON oBtn1 PROMPT "Confirmar" SIZE 50,15 OF oDlg PIXEL ;
        ACTION MsgInfo("Nome informado: " + AllTrim(cNome))

    ACTIVATE MSDIALOG oDlg CENTERED

Return

// telas customizadas em ADVPL para processos específicos, com interface
 simples e objetiva, reduzindo dependência de controles manuais fora do ERP
