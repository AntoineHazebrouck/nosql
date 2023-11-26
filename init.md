```
docker run --name monmongo -v mes_data_mongo:/data/db -p 27017:27017 -d mongo:latest

docker exec -i monmongo mongoimport -d test -c employes < tp1/employes.json

docker exec -it monmongo mongosh
```