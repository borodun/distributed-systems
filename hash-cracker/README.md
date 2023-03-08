# Hash cracker

Distributed system to crack hashes. Consists of one **manager** service and multiple **worker** services.

# Docker

## Build images

Build **manager**:
```bash
docker build \
  --build-arg SERVICE=manager \
  --build-arg SERVICE_PORT=8080 \
  -t manager \
  -f=docker/Dockerfile .
```

Build **worker**:
```bash
docker build \
  --build-arg SERVICE=worker \
  -t worker \
  -f=docker/Dockerfile .
```
## Running

Run **manager**:
```bash
docker run \
  -p 8080:8080 \
  -e WORKER_ADDR="http://worker:9080" \
  -e ALPHABET="abcde1234" \
  -e TASK_COUNT="30" \
  manager:latest
```

Run **worker**:
```bash
docker run \
  -e MANAGER_ADDR="http://manager:8080" \
  worker:latest
```

Run in Docker Compose:
```bash
docker compose -f docker/docker-compose.yml up --build
```

# Testing

Send task to crack hash:
```bash
MSG="abcd"
HASH=$(echo -n ${MSG} | md5sum | awk '{print $1}')
REQUEST_ID=$(curl -X POST -H "Content-Type: application/json" -d '{"hash": "'${HASH}'", "maxLength": '${#MSG}'}' localhost:8080/api/hash/crack)
```

Check task status:
```bash
curl "localhost:8080/api/hash/status?requestId=${REQUEST_ID}"
```

Should return the following object:
```json
{
   "status":"READY",
   "data": ["abcd"]
}
```