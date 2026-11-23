#!/usr/bin/env bash
# Prueba rapida de los 3 servicios (requiere que esten corriendo). Uso: bash probar.sh
set -e
echo "== predios: lista =="
curl -s localhost:8081/api/predios; echo
echo "== predios: crear =="
curl -s -X POST localhost:8081/api/predios -H 'Content-Type: application/json' \
  -d '{"nombre":"Predio Prueba","superficieHa":3.2}'; echo
echo "== usuarios: registro =="
curl -s -X POST localhost:8082/api/usuarios/registro -H 'Content-Type: application/json' \
  -d '{"nombre":"Persona Prueba","correo":"prueba@correo.cl","password":"Clave1234","rol":"AGRICULTOR"}'; echo
echo "== usuarios: login =="
curl -s -X POST localhost:8082/api/usuarios/login -H 'Content-Type: application/json' \
  -d '{"correo":"prueba@correo.cl","password":"Clave1234","rol":"AGRICULTOR"}'; echo
echo "== mediciones: sync (lo que manda la app) =="
curl -s -X POST localhost:8083/api/mediciones/sync -H 'Content-Type: application/json' \
  -d '[{"idLocal":99,"predioId":1,"cultivo":"Tomate","etapa":"Engorde","ph":6.5,"ce":1.5,"n":40,"p":20,"k":200,"ca":150,"mg":50,"fechaMillis":1760000000000,"validada":false}]'; echo
echo "== mediciones: resumen =="
curl -s localhost:8083/api/mediciones/resumen; echo
