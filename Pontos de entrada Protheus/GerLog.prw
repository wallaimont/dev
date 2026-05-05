#Include "protheus.ch"

User Function ZZGerLog(cMsg)
    Local cLog := ""

    cLog += Dtoc(Date()) + " " + Time() + " - " + cMsg + CRLF

    MemoWrite("C:\TEMP\PROTHEUS_LOG.TXT", cLog)

Return

//* Geração de log técnico com data/hora
sobrescreve o arquivo. Em projeto real, normalmente você:
lê o conteúdo atual
concatena
grava novamente
ou usa outra estratégia centralizada de log. *//
