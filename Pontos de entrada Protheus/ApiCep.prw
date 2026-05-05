#Include "protheus.ch"
#Include "parmtype.ch"
#Include "json.ch"

User Function ZZApiCep()
    Local oRest   := Nil
    Local cUrl    := "https://viacep.com.br/ws/01001000/json/"
    Local cRet    := ""
    Local oJson   := JsonObject():New()

    oRest := FWRest():New(cUrl)

    If oRest:Get()
        cRet := oRest:GetResult()

        oJson:FromJson(cRet)

        MsgInfo("Logradouro: " + oJson["logradouro"] + CRLF + ;
                "Bairro: " + oJson["bairro"] + CRLF + ;
                "Cidade: " + oJson["localidade"])
    Else
        MsgStop("Erro ao consumir API.")
    EndIf

Return

// integrações REST em ADVPL consumindo APIs, tratando JSON, autenticação e 
logs para garantir comunicação estável entre Protheus e sistemas externos
