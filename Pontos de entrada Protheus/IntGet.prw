#Include "protheus.ch"
#Include "json.ch"

User Function ZZIntGet()
    Local oRest := FWRest():New("https://viacep.com.br/ws/01001000/json/")
    Local cRet  := ""

    If oRest:Get()
        cRet := oRest:GetResult()
        MemoWrite("C:\TEMP\RETORNO_API.TXT", cRet)
        MsgInfo("Integração realizada com sucesso.")
    Else
        MemoWrite("C:\TEMP\RETORNO_API.TXT", "Erro na integração.")
        MsgStop("Falha ao consumir API.")
    EndIf

Return

//Consumo REST com log de retorno Além do consumo da API, sempre 
implemento log para rastreabilidade, suporte e reprocesso
