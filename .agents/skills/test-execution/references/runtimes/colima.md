# Colima Runtime

Load this reference only when a macOS backend-integration execution item needs
a Docker-compatible Colima runtime.

1. Read `Tests/README.md` and the project runner before setting environment
   variables; prefer the runner's automatic detection.
2. Check `colima status` and `docker info` without changing state.
3. Start Colima only when the user requested execution and the runtime is not
   running:

```bash
colima start --runtime docker
docker info
```

4. If the runner cannot detect the socket, use the project-documented values;
   typical settings are:

```bash
export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
```

Report runtime failures as `BLOCKED`; do not skip the selected integration
item or claim unit-test evidence as a substitute.
