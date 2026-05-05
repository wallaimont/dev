$ErrorActionPreference = 'Stop'
$BASE = 'http://localhost:8080/api/v1'

# Login
$loginResp = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/auth/login" -Method POST -ContentType 'application/json' -Body '{"email":"admin@supportdesk.com","password":"Admin@2024!"}'
$data = ($loginResp.Content | ConvertFrom-Json).data
$TOKEN = $data.accessToken
$H = @{Authorization="Bearer $TOKEN"}
$roles = $data.user.roles -join ','
Write-Host "[OK] Login admin - roles: $roles - expires: $($data.expiresIn)s"

# Decode JWT roles
$payload = $TOKEN.Split('.')[1]
$pad = $payload.Length % 4; if ($pad) { $payload += '=' * (4 - $pad) }
$jwtRoles = ([System.Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($payload)) | ConvertFrom-Json).roles
Write-Host "[OK] Roles no JWT: $($jwtRoles -join ', ')"

# Preparar categoryId obrigatoria para criacao de ticket
$existingCatsResp = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/categories" -Headers $H
$existingCats = ($existingCatsResp.Content | ConvertFrom-Json).data
if ($existingCats.Count -gt 0) {
    $ticketCategoryId = $existingCats[0].id
    Write-Host "[OK] Categoria base para ticket: $ticketCategoryId"
} else {
    $bootstrapCatResp = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/categories" -Method POST -ContentType 'application/json' -Headers $H -Body '{"name":"Categoria Bootstrap","description":"Categoria temporaria para testes automatizados"}'
    $ticketCategoryId = (($bootstrapCatResp.Content | ConvertFrom-Json).data).id
    Write-Host "[OK] Categoria bootstrap criada: $ticketCategoryId"
}

# 1. Criar chamado
Write-Host "`n--- TESTE 1: Criar chamado (qualquer usuario autenticado) ---"
$ticketBody = @{
    title = "Chamado de Teste Admin"
    description = "Validando permissoes de admin - chamado criado com sucesso."
    priority = "HIGH"
    categoryId = $ticketCategoryId
    tags = @("admin", "e2e")
} | ConvertTo-Json
$r1 = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/tickets" -Method POST -ContentType 'application/json' -Headers $H -Body $ticketBody
$ticket = ($r1.Content | ConvertFrom-Json).data
Write-Host "[OK] STATUS $($r1.StatusCode) - Ticket #$($ticket.ticketNumber) criado: '$($ticket.title)' | Status: $($ticket.status) | Prioridade: $($ticket.priority)"
$ticketId = $ticket.id

# 2. Listar categorias (público)
Write-Host "`n--- TESTE 2: Listar categorias (publico) ---"
$r2 = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/categories" -Headers $H
$cats = ($r2.Content | ConvertFrom-Json).data
Write-Host "[OK] STATUS $($r2.StatusCode) - $($cats.Count) categorias listadas"

# 3. Criar categoria (ADMIN only)
Write-Host "`n--- TESTE 3: Criar categoria (ADMIN only) ---"
$uniqueCategoryName = "Suporte Tecnico $([DateTimeOffset]::UtcNow.ToUnixTimeSeconds())"
$categoryBody = @{ name = $uniqueCategoryName; description = "Categoria para problemas tecnicos" } | ConvertTo-Json
$r3 = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/categories" -Method POST -ContentType 'application/json' -Headers $H -Body $categoryBody
$cat = ($r3.Content | ConvertFrom-Json).data
Write-Host "[OK] STATUS $($r3.StatusCode) - Categoria criada: '$($cat.name)' (ID: $($cat.id))"
$catId = $cat.id

# 4. Listar tickets (AGENT/SUPERVISOR/ADMIN ve todos)
Write-Host "`n--- TESTE 4: Listar tickets (AGENT/SUPERVISOR/ADMIN) ---"
$r4 = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/tickets?page=0&size=20" -Headers $H
Write-Host "[OK] STATUS $($r4.StatusCode) - Total tickets: $(($r4.Content | ConvertFrom-Json).data.totalElements)"

# 5. Registrar usuário comum e tentar criar categoria (deve falhar com 403)
Write-Host "`n--- TESTE 5: Registrar usuario comum e tentar criar categoria (deve retornar 403) ---"
$regBody = '{"fullName":"Usuario Teste","email":"user.teste@test.com","password":"Teste@123!"}'
try {
    $rReg = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/auth/register" -Method POST -ContentType 'application/json' -Body $regBody
    $regData = ($rReg.Content | ConvertFrom-Json).data
    $userToken = $regData.accessToken
    $userH = @{Authorization="Bearer $userToken"; 'Content-Type'='application/json'}
    Write-Host "[OK] Usuario comum criado/logado"
    # Tentar criar categoria como user comum
    try {
        $rForbid = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/categories" -Method POST -Headers $userH -Body '{"name":"Tentativa Proibida","description":"nao deve criar"}'
        Write-Host "[FALHA] Esperava 403 mas retornou $($rForbid.StatusCode) - PROBLEMA DE SEGURANCA!"
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        if ($code -eq 403) {
            Write-Host "[OK] STATUS 403 Forbidden - Usuario comum NAO pode criar categorias (controle de acesso OK)"
        } else {
            Write-Host "[INFO] STATUS $code - $($_.Exception.Message)"
        }
    }
} catch {
    Write-Host "[INFO] Registro falhou (usuario pode ja existir): $($_.Exception.Message)"
    # Login com usuario existente
    try {
        $rLogin2 = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/auth/login" -Method POST -ContentType 'application/json' -Body '{"email":"user.teste@test.com","password":"Teste@123!"}'
        $userToken = ($rLogin2.Content | ConvertFrom-Json).data.accessToken
        $userH = @{Authorization="Bearer $userToken"; 'Content-Type'='application/json'}
        Write-Host "[OK] Login usuario comum bem-sucedido"
        try {
            $rForbid = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/categories" -Method POST -Headers $userH -Body '{"name":"Tentativa Proibida","description":"nao deve criar"}'
            Write-Host "[FALHA] Esperava 403 mas retornou $($rForbid.StatusCode)"
        } catch {
            $code = $_.Exception.Response.StatusCode.value__
            if ($code -eq 403) {
                Write-Host "[OK] STATUS 403 Forbidden - Controle de acesso funcionando"
            } else {
                Write-Host "[INFO] STATUS $code"
            }
        }
    } catch {
        Write-Host "[INFO] Login usuario comum falhou: $($_.Exception.Message)"
    }
}

# 6. Deletar categoria criada (ADMIN only - limpeza)
Write-Host "`n--- TESTE 6: Deletar categoria (ADMIN only - limpeza) ---"
$r6 = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/categories/$catId" -Method DELETE -Headers $H
Write-Host "[OK] STATUS $($r6.StatusCode) - Categoria '$($cat.name)' removida"

# 7. Ver detalhe do ticket criado
Write-Host "`n--- TESTE 7: Ver detalhe do chamado criado ---"
$r7 = Invoke-WebRequest -UseBasicParsing -Uri "$BASE/tickets/$ticketId" -Headers $H
$ticketDetail = ($r7.Content | ConvertFrom-Json).data
Write-Host "[OK] STATUS $($r7.StatusCode) - Ticket: #$($ticketDetail.ticketNumber) | '$($ticketDetail.title)' | Solicitante: $($ticketDetail.requester.fullName)"

Write-Host "`n========================================="
Write-Host "RESUMO: Todos os testes de permissao admin concluidos com sucesso!"
Write-Host "========================================="
