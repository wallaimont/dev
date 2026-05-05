#Include "protheus.ch"
#Include "json.ch"

User Function ZZEnvPed()
    Local oRest := FWRest():New("https://meuservico/api/pedido")
    Local oJson := JsonObject():New()
    Local cBody := ""

    oJson["filial"]  := xFilial("SC5")
    oJson["pedido"]  := "000999"
    oJson["cliente"] := "000001"
    oJson["valor"]   := 999.99

    cBody := oJson:ToJson()

    oRest:SetHeader("Content-Type", "application/json")
    oRest:SetPostParams(cBody)

    If oRest:Post()
        MsgInfo("Pedido enviado com sucesso.")
    Else
        MsgStop("Falha ao enviar pedido.")
    EndIf

Return

//Envio REST com JSON de pedido Integração de saída com JSON e POST
