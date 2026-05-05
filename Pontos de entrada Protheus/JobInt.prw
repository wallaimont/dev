#Include "protheus.ch"

/*/{Protheus.doc} ZZJobInt
    Exemplo de job para processamento autom√°tico.
@type  Function
/*/
User Function ZZJobInt()
    Local cLog := ""

    // Marca in√≠cio do processamento
    cLog += "In√≠cio: " + Dtoc(Date()) + " " + Time() + CRLF

    // Aqui poderia chamar uma integra√ß√£o real
    // U_ZZConsomeApi()

    // Marca fim do processamento
    cLog += "Fim: " + Dtoc(Date()) + " " + Time() + CRLF

    // Grava log do job
    MemoWrite("C:\TEMP\JOB_INTEGRACAO.TXT", cLog)

Return


/*/ Job de integra√ß√£o com log
rotinas batch para execuÁ„o via job/schedule no AppServer, 
normalmente usadas para integraÁ„o, conciliaÁ„o ou processamento noturno /*/
