# Format Scala code using scalafmt.
#
# Usage: scalafmt [--test]
#
# no parameters: format files
#      "--test": test correctness

param (
    [ValidateSet("--test")] 
    [string]$testMode
)

$SCALAFMT_VERSION="0.6.8"
$SCALAFMT="$PSScriptRoot\.scalafmt-$SCALAFMT_VERSION.jar"
$SCALAFMTTEST="$PSScriptRoot\.scalafmt-CI-$SCALAFMT_VERSION.jar"
$COURSIER="$PSScriptRoot/coursier.ps1"

Try
{
    $ScalaFmtRun = ""
    if ($testMode) {
        &$COURSIER bootstrap com.geirsson:scalafmt-cli_2.11:$SCALAFMT_VERSION --main org.scalafmt.cli.Cli -o $SCALAFMTTEST
        $ScalaFmtRun = $SCALAFMTTEST
    }
    else {
        &$COURSIER bootstrap --standalone com.geirsson:scalafmt-cli_2.11:$SCALAFMT_VERSION -o $SCALAFMT -f --main org.scalafmt.cli.Cli
        $ScalaFmtRun = $SCALAFMT
    }

    $scalafmtExists = Test-Path $ScalaFmtRun
    if ($scalafmtExists -ne $True)
    {
        throw [System.IO.FileNotFoundException] "$ScalaFmtRun not found."
    }

    if ($testMode) {
        &java -jar $ScalaFmtRun $testMode
    }
    else {
        &java -jar $ScalaFmtRun
    }
}
Catch
{
    $ErrorMessage = $_.Exception.Message
    Write-Host $ErrorMessage
    exit 1
}