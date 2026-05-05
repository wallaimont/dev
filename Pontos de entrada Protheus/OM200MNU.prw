#Include "protheus.ch"

/*/{Protheus.doc} OM200MNU
    Adiciona botão de resumo da carga na rotina logística.
@type  Function
/*/
User Function OM200MNU()
    Local aRotina := {}

    // Botão adicional no menu
    AAdd(aRotina, {"Resumo da Carga", "U_ZZResumoCarga()", 0, 2, 0, Nil})

Return aRotina

//Menu customizado no OM200
