### Preparing DOCKER image ###

docker build -t ivangorbunovv/job-portal-api-image .
docker tag ivangorbunovv/job-portal-api-image ivangorbunovv/job-portal-api:job-portal-api-image
docker push ivangorbunovv/job-portal-api:job-portal-api-image


### Kubernetes operations ###

kubectl apply -f kubernetes\deploy-and-service-for-postgres.yaml
kubectl apply -f kubernetes\deploy-and-service-for-kafka.yaml
kubectl apply -f kubernetes\deploy-and-service-for-job-portal-api.yaml
