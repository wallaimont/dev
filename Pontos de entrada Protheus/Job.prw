#Include "protheus.ch"

User Function ZZJob()
    Local cLog := ""

    cLog += "Início do processamento: " + Dtoc(Date()) + " " + Time() + CRLF

    DbSelectArea("SA1")
    SA1->(DbSetOrder(1))
    SA1->(DbGoTop())

    While !SA1->(Eof())
        cLog += "Cliente: " + SA1->A1_COD + " - " + AllTrim(SA1->A1_NOME) + CRLF
        SA1->(DbSkip())
    EndDo

    MemoWrite("C:\TEMP\LOG_CLIENTES.TXT", cLog)

Return

// schedule job para processamento autom�tico rotina batch que pode ser executada por job no AppServer, com geração 
de log para rastreabilidade
