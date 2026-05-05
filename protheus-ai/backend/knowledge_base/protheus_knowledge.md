# Base de Conhecimento TOTVS Protheus

## 1. VISÃO GERAL DO SISTEMA

O TOTVS Protheus é o principal ERP (Enterprise Resource Planning) desenvolvido pela TOTVS S.A., líder em tecnologia para gestão empresarial no Brasil. O sistema cobre diversas áreas de negócio e é amplamente utilizado por empresas de pequeno, médio e grande porte.

### Componentes Principais
- **AppServer**: Servidor de aplicação Protheus (ERP Server)
- **SmartClient**: Cliente desktop para acesso ao sistema
- **WebClient**: Acesso via navegador web
- **DBAccess**: Camada de acesso a banco de dados
- **Broker**: Servidor de balanceamento de carga
- **License Server**: Servidor de licenças

### Bancos de Dados Suportados
- Microsoft SQL Server
- Oracle Database
- IBM DB2
- PostgreSQL
- MySQL/MariaDB

---

## 2. LINGUAGENS DE PROGRAMAÇÃO

### AdvPL (Advanced Protheus Language)
AdvPL é a linguagem clássica do Protheus, baseada em xBase/Clipper com extensões orientadas a objetos.

**Estrutura básica de uma função AdvPL:**
```advpl
#Include "Protheus.ch"
#Include "FWMVCDef.ch"

User Function MinhaFuncao()
    Local cVar    := ""
    Local nNumero := 0
    Local lLogico := .F.
    Local dData   := Date()
    Local aArray  := {}
    
    cVar := "Hello Protheus"
    MsgInfo(cVar, "Título")
    
Return NIL
```

**Convenção de Nomenclatura de Variáveis:**
- `c` = Character (string)
- `n` = Numeric
- `l` = Logical (.T./.F.)
- `d` = Date
- `a` = Array
- `o` = Object
- `u` = Undefined/Any
- `b` = Code Block

**Funções Essenciais AdvPL:**
- `MsgInfo(cMsg, cTitulo)` - Exibe mensagem informativa
- `MsgAlert(cMsg, cTitulo)` - Mensagem de alerta
- `MsgYesNo(cMsg, cTitulo)` - Pergunta Sim/Não
- `AllTrim(cStr)` - Remove espaços no início e fim
- `SubStr(cStr, nInicio, nTam)` - Substring
- `Len(cStr)` - Comprimento da string
- `Val(cStr)` - Converte string para número
- `Str(nNum, nTam, nDec)` - Converte número para string
- `DtoC(dData)` - Data para string
- `CtoD(cData)` - String para data
- `Date()` - Data atual
- `Time()` - Hora atual
- `Recno()` - Número do registro atual
- `EOF()` - Fim do arquivo
- `BOF()` - Início do arquivo

### TLPP (TL++ Plus Plus)
TLPP é a linguagem moderna do Protheus, orientada a objetos com tipagem estática.

**Estrutura básica TLPP:**
```tlpp
#Include "tlpp-core.th"
#Include "tlpp-object.th"

namespace myapp

class MinhaClasse

    data cNome as character
    data nValor as numeric
    data lAtivo as logical

    method new() constructor
    method getNome() as character
    method calcular(nBase as numeric) as numeric

end class

method new() class MinhaClasse
    self:cNome  := ""
    self:nValor := 0
    self:lAtivo := .T.
Return self

method getNome() as character class MinhaClasse
Return self:cNome

method calcular(nBase as numeric) as numeric
    local nResult as numeric
    nResult := nBase * self:nValor
Return nResult
```

**Tipos de dados TLPP:**
- `character` ou `string`
- `numeric` ou `integer` ou `float`
- `logical` ou `boolean`
- `date`
- `datetime`
- `array`
- `object`
- `variant` (qualquer tipo)

---

## 3. MÓDULOS DO PROTHEUS

### SIGAFIN - Módulo Financeiro
**Tabelas Principais:**
- `SE1` - Contas a Receber
- `SE2` - Contas a Pagar
- `SE5` - Movimentação Bancária
- `SA6` - Bancos
- `SE8` - Conciliação Bancária
- `SA7` - Portadores (Formas de Pagamento)

**Funções Importantes:**
- `Mata040()` - Baixa de Títulos a Receber
- `Mata090()` - Baixa de Títulos a Pagar
- `Mata460()` - Transferência Bancária
- `ExistCpo()` - Verifica existência de campo
- `GetSX3Cache()` - Obtém metadados do campo

### SIGAFIS - Módulo Fiscal
**Tabelas Principais:**
- `SF1` - Cabeçalho de Notas Fiscais de Entrada
- `SF2` - Cabeçalho de Notas Fiscais de Saída
- `SD1` - Itens de Notas Fiscais de Entrada
- `SD2` - Itens de Notas Fiscais de Saída
- `SF4` - Tipos de Entrada e Saída (TES)
- `SB5` - Complemento de Produto (Fiscal)
- `SFB` - Participantes do SPED

**Funções Fiscais:**
- `MaFisRef(cCampo, cAlias, nItem)` - Referencia campos fiscais
- `MaFisNF()` - Objeto de Nota Fiscal
- `TesNf(cTes)` - Valida TES
- `CalcImposto()` - Cálculo de impostos

### SIGACON - Módulo Contábil
**Tabelas Principais:**
- `CT1` - Plano de Contas
- `CT2` - Lançamentos Contábeis
- `CT5` - Centro de Custo
- `CTB` - Histórico Contábil

### SIGAEST - Gestão de Estoque
**Tabelas Principais:**
- `SB1` - Cadastro de Produtos
- `SB2` - Saldo de Estoque
- `SB5` - Complemento de Produtos
- `SD3` - Movimentações Internas
- `SBF` - Estrutura de Produto (BOM)

**Funções de Estoque:**
- `Mata250()` - Consulta de Saldo
- `AddSaldo()` - Adiciona saldo
- `RetSaldo()` - Retira saldo
- `MSNFEntr()` - Entrada de NF de Estoque

### SIGACOM - Compras
**Tabelas Principais:**
- `SC1` - Solicitação de Compra
- `SC7` - Pedido de Compra
- `SA2` - Cadastro de Fornecedores
- `SC5` - Pedido de Venda (ref. compras)

### SIGAVND - Vendas / Faturamento
**Tabelas Principais:**
- `SA1` - Cadastro de Clientes
- `SC5` - Pedido de Venda
- `SC6` - Itens do Pedido de Venda
- `SF2` - Nota Fiscal de Saída
- `SD2` - Itens de NF de Saída

### SIGAPON - Ponto Eletrônico / RH
**Tabelas Principais:**
- `SRA` - Funcionários
- `SRB` - Departamentos
- `SRC` - Cargos
- `SRD` - Dependentes
- `SRP` - Ficha Financeira (RH)
- `SRR` - Marcações de Ponto

### SIGAGPE - Gestão de Pessoal
**Tabelas Principais:**
- `SRA` - Funcionários
- `SRJ` - Eventos de Folha
- `SRR` - Marcações
- `SRZ` - Resultado de Cálculos

---

## 4. ACESSO A BANCO DE DADOS (DBF/SQL)

### Operações Básicas

**Selecionando área de trabalho:**
```advpl
DbSelectArea("SA1")
SA1->(DbSetOrder(1))  // Índice 1 (A1_FILIAL + A1_COD)
```

**Pesquisando registro:**
```advpl
// MsSeek - pesquisa com índice
If MsSeek(xFilial("SA1") + "000001")
    MsgInfo("Cliente: " + AllTrim(SA1->A1_NOME))
EndIf
```

**Bloqueio e alteração:**
```advpl
DbSelectArea("SA1")
SA1->(DbSetOrder(1))
If MsSeek(xFilial("SA1") + cCodCli)
    RecLock("SA1", .F.)  // .F. = não é novo registro
    SA1->A1_EMAIL := cNovoEmail
    SA1->(MsUnLock())
EndIf
```

**Incluindo novo registro:**
```advpl
DbSelectArea("SA1")
RecLock("SA1", .T.)  // .T. = novo registro
SA1->A1_FILIAL := xFilial("SA1")
SA1->A1_COD    := GetSXENum("SA1", "A1_COD")
SA1->A1_NOME   := "NOVO CLIENTE"
SA1->A1_CGC    := "12.345.678/0001-00"
SA1->(MsUnLock())
```

**Deletando registro:**
```advpl
DbSelectArea("SA1")
SA1->(DbSetOrder(1))
If MsSeek(xFilial("SA1") + cCodCli)
    RecLock("SA1", .F.)
    SA1->(DbDelete())
    SA1->(MsUnLock())
EndIf
```

**Laço (loop) em tabela:**
```advpl
DbSelectArea("SB1")
SB1->(DbSetOrder(1))
SB1->(DbGoTop())

While !SB1->(EOF()) .And. SB1->B1_FILIAL == xFilial("SB1")
    cCodProd := AllTrim(SB1->B1_COD)
    cDesProd := AllTrim(SB1->B1_DESC)
    SB1->(DbSkip())
End
```

**Query SQL no Protheus:**
```advpl
Local cQuery as character
Local oSQL   as object

cQuery := "SELECT A1_COD, A1_NOME, A1_CGC "
cQuery += "FROM " + RetSqlName("SA1") + " SA1 "
cQuery += "WHERE SA1.D_E_L_E_T_ = ' ' "
cQuery += "AND SA1.A1_FILIAL = '" + xFilial("SA1") + "' "
cQuery += "ORDER BY SA1.A1_NOME"

oSQL := TCQuery(cQuery, .T., .T.)

While !oSQL:EOF()
    cCod  := oSQL:A1_COD
    cNome := oSQL:A1_NOME
    oSQL:Skip()
End
oSQL:Close()
```

---

## 5. TRATAMENTO DE ERROS

```advpl
Begin Sequence
    // Código que pode gerar erro
    DbSelectArea("SA1")
    SA1->(RecLock("SA1", .F.))
    SA1->A1_NOME := "TESTE"
    SA1->(MsUnLock())
Recover Sequence
    // Tratamento do erro
    MsgAlert("Erro ao processar: " + ErrorMessage())
End Sequence
```

---

## 6. FRAMEWORK MVC DO PROTHEUS (FWModel/FWBrowse)

### Estrutura MVC
O Protheus possui um framework próprio MVC baseado em:
- **Model**: `FWFormModel` / `FWMVCModel`
- **View**: `FWFormView` / `FWBrwHelp`
- **Controller**: Funções de ativação

**Exemplo de CRUD com MVC:**
```advpl
#Include "Protheus.ch"
#Include "FWMVCDef.ch"

User Function MEUBRW()
    Local oBrowse as object
    
    oBrowse := FWMBrowse():New()
    oBrowse:SetAlias("SB1")
    oBrowse:SetDescription("Cadastro de Produtos")
    oBrowse:Activate()
Return

Static Function ModelDef()
    Local oModel as object
    
    oModel := MPFormModel():New("MEUMOD01", , {|oModel| ModelValid(oModel)})
    oModel:SetPrimaryKey({"B1_FILIAL", "B1_COD"})
    oModel:AddFields("MASTERFIELDS", , "SB1")
    oModel:SetDescription("Cadastro de Produtos")
Return oModel
```

---

## 7. APIs REST DO PROTHEUS

### Configuração do Servidor REST
No arquivo `appserver.ini`:
```ini
[HTTPV11]
Enable=1
Sockets=HTTPSRV

[HTTPSRV]
Port=8080
MaxQueue=100
```

### Criando um Endpoint REST em TLPP
```tlpp
#Include "tlpp-core.th"
#Include "tlpp-object.th"

@RestResource( uriTemplate = "/produtos/{id}" )
class ProductResource

    @HttpGet
    method get(id as character) as character
        local cResponse as character
        local oJson     as object
        local oProduct  as object
        
        oJson := JsonObject():New()
        
        DbSelectArea("SB1")
        SB1->(DbSetOrder(1))
        
        If MsSeek(xFilial("SB1") + id)
            oJson:SetJsonText("codigo", AllTrim(SB1->B1_COD))
            oJson:SetJsonText("descricao", AllTrim(SB1->B1_DESC))
            oJson:SetJsonNum("preco", SB1->B1_PRV1)
            SetRestResponse(200, oJson:Serialize())
        Else
            oJson:SetJsonText("erro", "Produto não encontrado")
            SetRestResponse(404, oJson:Serialize())
        EndIf
        
    Return cResponse

    @HttpPost
    method post() as character
        local cBody  as character
        local oJson  as object
        local cCod   as character
        
        cBody := GetRestMessage()
        oJson := JsonObject():New()
        oJson:DeserializeStr(cBody)
        
        cCod := oJson:GetJsonText("codigo")
        
        DbSelectArea("SB1")
        RecLock("SB1", .T.)
        SB1->B1_FILIAL := xFilial("SB1")
        SB1->B1_COD    := cCod
        SB1->B1_DESC   := oJson:GetJsonText("descricao")
        SB1->B1_PRV1   := oJson:GetJsonNum("preco")
        SB1->(MsUnLock())
        
        SetRestResponse(201, '{"status":"criado"}')
    Return ""

end class
```

### Consumindo API REST no AdvPL
```advpl
#Include "Protheus.ch"

User Function ConsumirAPI()
    Local oHttp   as object
    Local cURL    as character
    Local cResult as character
    Local oJson   as object
    
    cURL  := "https://api.exemplo.com/produtos/001"
    oHttp := FWHTTPRequest():New()
    
    oHttp:SetUrl(cURL)
    oHttp:SetMethod("GET")
    oHttp:AddHeader("Content-Type", "application/json")
    oHttp:AddHeader("Authorization", "Bearer " + GetToken())
    
    If oHttp:Execute()
        cResult := oHttp:GetResponseBody()
        oJson   := JsonObject():New()
        oJson:DeserializeStr(cResult)
        MsgInfo("Produto: " + oJson:GetJsonText("descricao"))
    Else
        MsgAlert("Erro: " + oHttp:GetError())
    EndIf
    
    oHttp:Destroy()
Return NIL
```

---

## 8. FUNÇÕES DE AMBIENTE E CONFIGURAÇÃO

```advpl
// Filial atual
xFilial("SA1")     // retorna filial formatada para tabela SA1
cFilAnt := FWxFilial("SA1")

// Empresa atual
cEmpresa := GETCURREMPRESA()

// Usuário logado
cUsuario := GETCURRUSER()

// Data do sistema
dDataSis := dDataBase  // variável global

// Caminho do sistema
cPathSis := GetSrvProfString("RootPath", "")

// Parâmetros do sistema (SX6)
cParam := GetMV("MV_CFOP")  // lê parâmetro MV_CFOP

// Numeração automática
cProxNum := GetSXENum("SA1", "A1_COD")
```

---

## 9. RELATÓRIOS E SPOOL

### Relatório com FWPrintSetup
```advpl
#Include "Protheus.ch"

User Function MeuRelatorio()
    Local oReport as object
    Local oSection as object
    
    oReport := TReport():New("MEUREL", "Meu Relatório", , {|oReport| ImpRel(oReport)})
    oReport:SetDescription("Relatório de Produtos")
    oSection := oReport:AddSection("PRINCIPAL")
    oSection:AddField("CODIGO", 10, "B1_COD", "Código")
    oSection:AddField("DESCRICAO", 40, "B1_DESC", "Descrição")
    oSection:AddField("PRECO", 15, "B1_PRV1", "Preço", , , .T.)
    
    oReport:PrintDialog()
Return

Static Function ImpRel(oReport)
    Local oSection := oReport:Section("PRINCIPAL")
    
    DbSelectArea("SB1")
    SB1->(DbSetOrder(1))
    SB1->(DbGoTop())
    
    While !SB1->(EOF()) .And. !oReport:Cancel()
        oSection:Init()
        oSection:Cell("CODIGO"):SetValue(SB1->B1_COD)
        oSection:Cell("DESCRICAO"):SetValue(SB1->B1_DESC)
        oSection:Cell("PRECO"):SetValue(SB1->B1_PRV1)
        oSection:Finish()
        SB1->(DbSkip())
    End
Return
```

---

## 10. TOTVS CAROL (Plataforma de IA)

TOTVS Carol é a plataforma de Inteligência Artificial e dados da TOTVS que se integra ao Protheus.

### Funcionalidades:
- **Data Pipeline**: ETL e transformação de dados do Protheus
- **Machine Learning**: Modelos preditivos sobre dados ERP
- **Analytics**: Dashboards e relatórios avançados
- **MDM**: Master Data Management

### Integração Carol-Protheus:
- Conector nativo via Message Broker
- APIs REST da Carol
- Staging Tables para ingestão de dados
- Golden Records para dados mestres consolidados

---

## 11. TOTVS FLUIG

Fluig é a plataforma de BPM (Business Process Management) e portal da TOTVS.

### Integração Fluig-Protheus:
- **Workflows**: Processos de aprovação integrados ao Protheus
- **ECM**: Gestão de documentos
- **Portal**: Acesso web a funcionalidades do Protheus
- **APIs**: REST APIs para consumo de dados do Protheus no Fluig

### Dataset Fluig consumindo Protheus:
```javascript
// Dataset no Fluig consultando dados do Protheus
function createDataset(fields, constraints, sortFields) {
    var dataset = DatasetBuilder.newDataset();
    dataset.addColumn("codigo");
    dataset.addColumn("descricao");
    
    var wsResult = ServiceManager.getService("ProtheusService");
    // Chamada ao webservice Protheus
    var result = wsResult.getClientPort().getProdutos();
    
    result.forEach(function(item) {
        dataset.addRow([item.codigo, item.descricao]);
    });
    
    return dataset;
}
```

---

## 12. CONFIGURAÇÃO DO APPSERVER

### Arquivo appserver.ini básico:
```ini
[General]
App=SIGAADV
RootPath=C:\TOTVS\Protheus_Data\

[Environment]
SourcePath=C:\TOTVS\Protheus_Data\apo\
RootPath=C:\TOTVS\Protheus_Data\
StartPath=\system\
RpoDb=Top
RpoLanguage=Portuguese
RpoVersion=120

[TCP]
Type=TCPIP
Port=1234
MaxConn=1000

[DRIVERS]
Active=TCP

[DBACCESS]
Server=localhost
Port=7890
Database=MSSQL
Alias=PROTHEUS12

[HTTPV11]
Enable=1
Sockets=HTTPSRV

[HTTPSRV]
Port=8080
MaxQueue=100
Threads=10
```

---

## 13. PONTOS DE ENTRADA (USER EXIT POINTS)

Pontos de entrada são hooks que permitem customização sem modificar o fonte original TOTVS.

**Exemplo de Ponto de Entrada:**
```advpl
// Ponto de entrada executado ao incluir cliente (SA1)
User Function A140INCL()
    Local oModel := FWModelActive()
    Local cCod   := oModel:GetValue("MASTERFIELDS", "A1_COD")
    
    // Validação customizada
    If Len(AllTrim(cCod)) < 3
        MsgAlert("Código deve ter pelo menos 3 caracteres")
        Return .F.
    EndIf
    
Return .T.

// Ponto de entrada no cálculo de impostos
User Function TESNF()
    // Customização de TES na NF
Return NIL
```

**Principais Pontos de Entrada por Módulo:**
- `A140INCL` / `A140ALT` / `A140EXC` - Clientes (SIGAVND)
- `A120INCL` / `A120ALT` - Fornecedores (SIGACOM)
- `MT100PEN` / `MT100OK` - Pedido de Venda (SIGAFAT)
- `M460A` / `M460B` - Nota Fiscal Saída
- `FINA040` - Baixa de Títulos
- `NF_CALCU` - Cálculo de Impostos NF

---

## 14. SPED / OBRIGAÇÕES FISCAIS

### EFD ICMS/IPI (SIGAEFD)
- **Registro 0** - Abertura e Identificação
- **Registro C** - Documentos Fiscais
- **Registro D** - Serviços de Transporte
- **Registro E** - Apuração de Impostos
- **Registro G** - Controle de Crédito CIAP
- **Registro H** - Inventário
- **Registro K** - Controle de Produção

### Configurações SPED:
- Parâmetros: `MV_ESTADO`, `MV_REGPIS`, `MV_REGTRIB`
- Tabelas: `SFB` (Participantes), `SFC` (Itens SPED)

---

## 15. SCHEDULADOR DE TAREFAS (SCHEDULE)

```advpl
// Função executada pelo Schedule do Protheus
User Function MYSCHEDULE()
    Local cLog as character
    
    cLog := "Início: " + Time() + Chr(13) + Chr(10)
    
    // Processamento
    DbSelectArea("SB1")
    SB1->(DbSetOrder(1))
    SB1->(DbGoTop())
    
    While !SB1->(EOF())
        // Processamento de cada produto
        SB1->(DbSkip())
    End
    
    cLog += "Fim: " + Time()
    
    // Grava log
    ApcMemo("MYSCHEDULE", cLog)
    
Return NIL
```

---

## 16. PERGUNTAS FREQUENTES (FAQ)

**P: Como criar um relatório customizado?**
R: Use o framework TReport ou FWPrintSetup. Para relatórios simples, use SetPrint/Rept. Para relatórios MVC use TReport.

**P: Como integrar Protheus com sistemas externos?**
R: Use as APIs REST nativas do Protheus (HTTPV11), Webservices SOAP, Message Broker, ou a plataforma TOTVS Carol.

**P: Como debugar código AdvPL/TLPP?**
R: Use o TDS (TOTVS Developer Studio) baseado em Eclipse ou VS Code com extensão TOTVS. Configure o debug no appserver.ini com [TotvsMonitor].

**P: Como consultar logs do AppServer?**
R: Os logs ficam em `<RootPath>\system\` com arquivos .log. Use o Console do TOTVS Monitor.

**P: Qual a diferença entre AdvPL e TLPP?**
R: AdvPL é a linguagem legada, procedural/OO. TLPP (TL++ Plus Plus) é a linguagem moderna, fortemente tipada, com suporte a namespaces, generics e padrões modernos. Ambas são compiladas para RPO (Repository Object).

**P: Como fazer deploy de customizações?**
R: Compile o fonte (.prw/.tlpp) no TDS e gere o patch (.ptm) para aplicar no RPO do servidor.

**P: Como configurar autenticação OAuth no Protheus?**
R: Configure no appserver.ini a seção [JWT] e use as funções JWTValidate() e JWTSign() no AdvPL/TLPP.

---

## 17. TABELAS GERAIS DO SISTEMA

| Alias | Descrição |
|-------|-----------|
| SA1 | Clientes |
| SA2 | Fornecedores |
| SA3 | Vendedores |
| SA4 | Transportadoras |
| SA5 | Contatos |
| SA6 | Bancos |
| SB1 | Produtos |
| SB2 | Saldos de Estoque |
| SB5 | Complemento de Produtos |
| SC1 | Solicitação de Compra |
| SC5 | Pedidos de Venda |
| SC6 | Itens de Pedido de Venda |
| SC7 | Pedidos de Compra |
| SD1 | Itens de NF Entrada |
| SD2 | Itens de NF Saída |
| SD3 | Movimentações Internas |
| SE1 | Contas a Receber |
| SE2 | Contas a Pagar |
| SE5 | Movimentação Bancária |
| SF1 | NF Entrada (Cabeçalho) |
| SF2 | NF Saída (Cabeçalho) |
| SF4 | Tipos de Entrada/Saída (TES) |
| SRA | Funcionários |
| SX3 | Dicionário de Dados (Campos) |
| SX5 | Tabelas do Sistema |
| SX6 | Parâmetros |
| SXA | Gatilhos |
| SXB | Consultas Padrão |
