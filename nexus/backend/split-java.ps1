param([string[]]$Files)

function Split-JavaFile {
    param([string]$Path)
    
    if (-not (Test-Path $Path)) { Write-Warning "Not found: $Path"; return }
    
    $lines = Get-Content $Path
    $dir = Split-Path $Path
    $fileName = Split-Path $Path -Leaf
    
    # Extract package line
    $pkgLine = ($lines | Where-Object { $_ -match '^\s*package\s+' } | Select-Object -First 1)
    
    # Extract import lines
    $importLines = @($lines | Where-Object { $_ -match '^\s*import\s+' })
    
    # Build header
    $header = $pkgLine + "`r`n"
    if ($importLines.Count -gt 0) {
        $header += "`r`n" + ($importLines -join "`r`n") + "`r`n"
    }
    
    # Parse top-level types
    $types = @()
    $currentLines = @()
    $typeName = ''
    $braceDepth = 0
    $inType = $false
    $pastHeader = $false
    
    foreach ($line in $lines) {
        $trimmed = $line.Trim()
        
        # Skip package/import lines
        if ($trimmed -match '^package\s' -or $trimmed -match '^import\s') { continue }
        
        # Before first type
        if (-not $inType) {
            # Skip empty lines and comment separators between types
            if ($trimmed -eq '' -or $trimmed -match '^//') {
                if ($currentLines.Count -eq 0) { continue }
                $hasAnnotation = $currentLines | Where-Object { $_.Trim() -match '^@' }
                if (-not $hasAnnotation) { 
                    $currentLines = @()
                    continue 
                }
                continue
            }
            
            # Check if this line contains a type declaration (could also have annotations inline)
            if ($trimmed -match '(?:^|\s)(public\s+)?(abstract\s+)?(class|enum|interface|record)\s+(\w+)') {
                $typeName = $Matches[4]
                $inType = $true
                $braceDepth = 0
            }
            elseif ($trimmed -match '^@') {
                # Pure annotation line (no class on same line)
                $currentLines += $line
                continue
            }
        }
        
        if ($inType) {
            $currentLines += $line
            
            # Count braces (simple - works for non-string-containing lines mostly)
            $opens = ([char[]]$line | Where-Object { $_ -eq '{' }).Count
            $closes = ([char[]]$line | Where-Object { $_ -eq '}' }).Count
            $braceDepth += ($opens - $closes)
            
            if ($braceDepth -le 0 -and $closes -gt 0) {
                # Type complete
                $types += [PSCustomObject]@{
                    Name = $typeName
                    Body = ($currentLines -join "`r`n")
                }
                $currentLines = @()
                $inType = $false
                $typeName = ''
                $braceDepth = 0
            }
        }
    }
    
    # Handle last type if file doesn't end with closing brace properly
    if ($typeName -and $currentLines.Count -gt 0) {
        $types += [PSCustomObject]@{
            Name = $typeName
            Body = ($currentLines -join "`r`n")
        }
    }
    
    if ($types.Count -le 1) {
        Write-Output "  $fileName - only 1 type, skipping"
        return
    }
    
    Write-Output "  $fileName - found $($types.Count) types:"
    
    foreach ($type in $types) {
        $dest = Join-Path $dir "$($type.Name).java"
        $content = $header + "`r`n" + $type.Body + "`r`n"
        [System.IO.File]::WriteAllText($dest, $content, [System.Text.UTF8Encoding]::new($false))
        Write-Output "    => $($type.Name).java"
    }
    
    # Delete original aggregate file 
    Remove-Item $Path -Force
    Write-Output "    (deleted $fileName)"
}

foreach ($f in $Files) {
    Write-Output "Processing: $f"
    Split-JavaFile -Path $f
}
