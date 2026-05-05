#Include "protheus.ch"

/*/{Protheus.doc} ZZValProd
    Valida campos importantes do cadastro de produtos.
@type  Function
/*/
User Function ZZValProd()
    Local lOk := .T.

    // Garante que estamos trabalhando na tabela SB1
    DbSelectArea("SB1")

    // Valida descrição do produto
    If Empty(AllTrim(SB1->B1_DESC))
        MsgStop("Descrição do produto é obrigatória.")
        lOk := .F.
    EndIf

    // Valida unidade de medida
    If Empty(AllTrim(SB1->B1_UM))
        MsgStop("Unidade de medida é obrigatória.")
        lOk := .F.
    EndIf

    // Valida tipo do produto
    If Empty(AllTrim(SB1->B1_TIPO))
        MsgStop("Tipo do produto é obrigatório.")
        lOk := .F.
    EndIf

Return lOk

//Validação de campos obrigatórios no produto validação para 
evitar cadastro inconsistente, porque erro em produto impacta 
compras, estoque, faturamento e fiscal
valida��es de campos obrigat�rios para reduzir cadastro inconsistente 
e evitar erro em compras, estoque e faturamento
