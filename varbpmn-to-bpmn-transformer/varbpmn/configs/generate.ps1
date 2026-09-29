param(
    [string]$ConfigPath = ".\default.xml",
    [string]$MappingPath = ".\feature_to_var.json",
    [string]$InputBpmnPath = "..\Koperasi.bpmn2",
    [string]$OutputBpmnPath = "..\Koperasi_generated.bpmn2"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$baseDir = (Get-Location).Path
$configFullPath = [System.IO.Path]::GetFullPath((Join-Path $baseDir $ConfigPath))
$mappingFullPath = [System.IO.Path]::GetFullPath((Join-Path $baseDir $MappingPath))
$inputBpmnFullPath = [System.IO.Path]::GetFullPath((Join-Path $baseDir $InputBpmnPath))
$outputBpmnFullPath = [System.IO.Path]::GetFullPath((Join-Path $baseDir $OutputBpmnPath))

function Test-IsSelectedValue {
    param([string]$Value)
    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $false
    }

    $norm = $Value.Trim().ToLowerInvariant()
    return ($norm -in @("selected", "true", "1", "manual"))
}

function Test-IsUnselectedValue {
    param([string]$Value)
    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $false
    }

    $norm = $Value.Trim().ToLowerInvariant()
    return ($norm -in @("unselected", "false", "0"))
}

function Get-SelectedFeatures {
    param([xml]$ConfigXml)

    $features = @()
    foreach ($featureNode in $ConfigXml.configuration.feature) {
        $featureName = [string]$featureNode.GetAttribute("name")
        if ([string]::IsNullOrWhiteSpace($featureName)) {
            continue
        }

        $automatic = [string]$featureNode.GetAttribute("automatic")
        $manual = [string]$featureNode.GetAttribute("manual")
        $selected = [string]$featureNode.GetAttribute("selected")
        $hasExplicitUnselected = (Test-IsUnselectedValue $automatic) -or (Test-IsUnselectedValue $manual) -or (Test-IsUnselectedValue $selected)
        $hasExplicitSelected = (Test-IsSelectedValue $automatic) -or (Test-IsSelectedValue $manual) -or (Test-IsSelectedValue $selected)

        # Treat missing selection attributes as unselected by default.
        $isSelected = $false
        if ($hasExplicitUnselected) {
            $isSelected = $false
        }
        elseif ($hasExplicitSelected) {
            $isSelected = $true
        }

        if ($isSelected) {
            $features += $featureName
        }
    }

    return $features
}

function Get-OrCreateExtensionElements {
    param(
        [xml]$BpmnXml,
        [System.Xml.XmlElement]$BpmnElement,
        [System.Xml.XmlNamespaceManager]$Ns
    )

    $existing = $BpmnElement.SelectSingleNode("bpmn2:extensionElements", $Ns)
    if ($null -ne $existing) {
        return [System.Xml.XmlElement]$existing
    }

    $ext = $BpmnXml.CreateElement("bpmn2", "extensionElements", "http://www.omg.org/spec/BPMN/20100524/MODEL")
    [void]$BpmnElement.PrependChild($ext)
    return $ext
}

function Remove-VariabilityAnnotations {
    param(
        [System.Xml.XmlElement]$ExtensionElements,
        [System.Xml.XmlNamespaceManager]$Ns
    )

    $nodes = $ExtensionElements.SelectNodes("sple:inclusionVariability | sple:connector | sple:receiver", $Ns)
    $toDelete = @()
    foreach ($n in $nodes) {
        $toDelete += $n
    }

    foreach ($n in $toDelete) {
        [void]$ExtensionElements.RemoveChild($n)
    }
}

if (-not (Test-Path -LiteralPath $configFullPath)) {
    throw "Config file not found: $configFullPath"
}
if (-not (Test-Path -LiteralPath $mappingFullPath)) {
    throw "Mapping file not found: $mappingFullPath"
}
if (-not (Test-Path -LiteralPath $inputBpmnFullPath)) {
    throw "Input BPMN file not found: $inputBpmnFullPath"
}

[xml]$configXml = Get-Content -Path $configFullPath -Raw
$map = Get-Content -Path $mappingFullPath -Raw | ConvertFrom-Json
[xml]$bpmnXml = Get-Content -Path $inputBpmnFullPath -Raw

$definitions = $bpmnXml.DocumentElement
if ($null -eq $definitions) {
    throw "Invalid BPMN document: missing root element."
}

$ns = New-Object System.Xml.XmlNamespaceManager($bpmnXml.NameTable)
$ns.AddNamespace("bpmn2", "http://www.omg.org/spec/BPMN/20100524/MODEL")
$ns.AddNamespace("sple", "http://sple/bpmn/extensions")
$ns.AddNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")

$selectedFeatures = Get-SelectedFeatures -ConfigXml $configXml

$allMappedIds = New-Object System.Collections.Generic.HashSet[string]
$updatesById = @{}

foreach ($featureProp in $map.PSObject.Properties) {
    foreach ($entry in $featureProp.Value) {
        if ($null -ne $entry.id) {
            [void]$allMappedIds.Add([string]$entry.id)
        }
    }
}

foreach ($featureName in $selectedFeatures) {
    if (-not ($map.PSObject.Properties.Name -contains $featureName)) {
        continue
    }

    foreach ($entry in $map.$featureName) {
        $id = [string]$entry.id
        if ([string]::IsNullOrWhiteSpace($id)) {
            continue
        }

        if (-not ($updatesById.ContainsKey($id))) {
            $updatesById[$id] = @{}
        }

        $hasInclusion = $entry.PSObject.Properties.Name -contains "inclusionVariability"
        $hasConnector = $entry.PSObject.Properties.Name -contains "connector"
        $hasReceiver = $entry.PSObject.Properties.Name -contains "receiver"

        if ($hasInclusion -and $null -ne $entry.inclusionVariability) {
            $updatesById[$id]["inclusionVariability"] = [string]$entry.inclusionVariability
        }

        if ($hasConnector -and $null -ne $entry.connector) {
            $updatesById[$id]["connector"] = @{
                name = [string]$entry.connector.name
                select = [string]$entry.connector.select
            }
        }

        if ($hasReceiver -and $null -ne $entry.receiver) {
            $receiverValues = @()
            foreach ($r in $entry.receiver) {
                $receiverValues += [string]$r
            }
            $updatesById[$id]["receiver"] = $receiverValues
        }
    }
}

foreach ($id in $allMappedIds) {
    $bpmnNode = $bpmnXml.SelectSingleNode("//*[@id='$id']", $ns)
    if ($null -eq $bpmnNode) {
        Write-Warning "Mapped id '$id' not found in input BPMN."
        continue
    }

    $ext = Get-OrCreateExtensionElements -BpmnXml $bpmnXml -BpmnElement $bpmnNode -Ns $ns
    Remove-VariabilityAnnotations -ExtensionElements $ext -Ns $ns

    if (-not $updatesById.ContainsKey($id)) {
        continue
    }

    $update = $updatesById[$id]

    if ($update.ContainsKey("inclusionVariability")) {
        $inc = $bpmnXml.CreateElement("sple", "inclusionVariability", "http://sple/bpmn/extensions")
        $inc.InnerText = [string]$update["inclusionVariability"]
        [void]$ext.AppendChild($inc)
    }

    if ($update.ContainsKey("connector")) {
        $connector = $update["connector"]
        $conn = $bpmnXml.CreateElement("sple", "connector", "http://sple/bpmn/extensions")

        $nameAttr = $bpmnXml.CreateAttribute("name")
        $nameAttr.Value = [string]$connector["name"]
        [void]$conn.Attributes.Append($nameAttr)

        $selectAttr = $bpmnXml.CreateAttribute("select")
        $selectAttr.Value = [string]$connector["select"]
        [void]$conn.Attributes.Append($selectAttr)

        [void]$ext.AppendChild($conn)
    }

    if ($update.ContainsKey("receiver")) {
        foreach ($receiverValue in $update["receiver"]) {
            $recv = $bpmnXml.CreateElement("sple", "receiver", "http://sple/bpmn/extensions")
            $recv.InnerText = [string]$receiverValue
            [void]$ext.AppendChild($recv)
        }
    }
}

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
$settings = New-Object System.Xml.XmlWriterSettings
$settings.Encoding = $utf8NoBom
$settings.Indent = $true
$settings.OmitXmlDeclaration = $false

$outputDir = [System.IO.Path]::GetDirectoryName($outputBpmnFullPath)
if (-not [string]::IsNullOrWhiteSpace($outputDir) -and -not (Test-Path -LiteralPath $outputDir)) {
    [void](New-Item -ItemType Directory -Path $outputDir -Force)
}

$writer = [System.Xml.XmlWriter]::Create($outputBpmnFullPath, $settings)
try {
    $bpmnXml.Save($writer)
}
finally {
    $writer.Close()
}

if (-not (Test-Path -LiteralPath $outputBpmnFullPath)) {
    throw "Failed to generate BPMN file at: $outputBpmnFullPath"
}

Write-Host "Selected features: $($selectedFeatures -join ', ')"
Write-Host "Generated BPMN: $outputBpmnFullPath"
