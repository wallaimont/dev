#Include "protheus.ch"
#Include "json.ch"

/*/{Protheus.doc} ZZConsomeApi
    Consome API REST e grava retorno em log.
@type  Function
/*/
User Function ZZConsomeApi()
    Local oRest := FWRest():New("https://viacep.com.br/ws/01001000/json/")
    Local cRet  := ""
    Local oJson := JsonObject():New()

    // Tenta executar GET
    If oRest:Get()

        // Captura resposta
        cRet := oRest:GetResult()

        // Grava log técnico
        MemoWrite("C:\TEMP\API_RETORNO.TXT", cRet)

        // Converte JSON em objeto
        oJson:FromJson(cRet)

        MsgInfo("Cidade retornada: " + oJson["localidade"])
    Else
        MemoWrite("C:\TEMP\API_RETORNO.TXT", "Erro ao consumir API.")
        MsgStop("Falha na integração.")
    EndIf

Return


// Consumo REST com JSON e log de retorno
