#Include "protheus.ch"

/*/{Protheus.doc} MT010MNU
    Adiciona botão customizado no menu do cadastro de produtos.
    Exemplo típico de uso de ponto de entrada sem alterar fonte padrão.
@type  Function
/*/
User Function MT010MNU()
    Local aRotina := {}

    // Adiciona nova opção ao menu da rotina padrão
    AAdd(aRotina, {"Validação do Produto", "U_ZZValProd()", 0, 2, 0, Nil})

Return aRotina

/*/Ponto de entrada no MATA010 com botão customizado ponto de entrada para incluir
 uma funcionalidade customizada dentro da rotina padrão, preservando compatibilidade 
 com atualização do Protheus/*/
