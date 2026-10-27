# Evidência de execução local em 27/10/2026

Comando: `.\mvnw.cmd clean verify` em `servicehub-api`.

Nesta sessão foi necessário informar também `-Dmaven.repo.local=C:\Users\Maryana.Melo\.m2\repository`, pois o ambiente de execução resolvia o cache padrão como `C:\.m2\repository`. O Java 21 foi selecionado por `JAVA_HOME` somente no processo de build.

Os arquivos `.txt` são cópias dos resumos Surefire produzidos pela execução final. `build-resumo.txt` contém as linhas de resultado e empacotamento extraídas do log. Eles registram os testes em H2, sem comprovar execução em PostgreSQL ou capturas manuais do Swagger.
