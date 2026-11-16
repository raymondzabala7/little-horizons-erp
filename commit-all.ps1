# Ejecutar en la raiz del proyecto (despues de copiar los archivos). Una linea por commit.
$steps = @(
 @("src/com/littlehorizons/erp/config src/com/littlehorizons/erp/exception src/com/littlehorizons/erp/util", "feat: add config, exceptions and utilities"),
 @("src/com/littlehorizons/erp/model", "feat(model): add domain records and catalog"),
 @("docs", "docs: add database.sql with DDL and test data"),
 @("src/com/littlehorizons/erp/dao", "feat(dao): add JDBC DAO layer"),
 @("src/com/littlehorizons/erp/service", "feat(service): add business services and invoice writer"),
 @("src/com/littlehorizons/erp/view src/com/littlehorizons/erp/controller src/com/littlehorizons/erp/Main.java", "feat: add console views, controllers and main menu"),
 @("README.md .gitignore config.properties.example logs invoices", "docs: add README and project files")
)
foreach ($s in $steps) { git add $s[0].Split(" "); git commit -m $s[1] }
git push origin main
