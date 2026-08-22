#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
LAYER="${1:-fast}"
JAVA_HOME_FALLBACK="/opt/homebrew/Cellar/openjdk/26.0.1/libexec/openjdk.jdk/Contents/Home"
COLIMA_DOCKER_SOCKET="$HOME/.colima/default/docker.sock"
BACKEND_ARTIFACTS_INSTALLED=0
JACOCO_VERSION="0.8.13"

if [[ -z "${JAVA_HOME:-}" && -d "$JAVA_HOME_FALLBACK" ]]; then
  export JAVA_HOME="$JAVA_HOME_FALLBACK"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

if [[ -z "${DOCKER_HOST:-}" && -S "$COLIMA_DOCKER_SOCKET" ]]; then
  export DOCKER_HOST="unix://$COLIMA_DOCKER_SOCKET"
  export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="${TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE:-/var/run/docker.sock}"
  export DOCKER_API_VERSION="${DOCKER_API_VERSION:-1.40}"
fi

ensure_backend_artifacts() {
  if [[ "${LITEMALL_TESTS_SKIP_BACKEND_INSTALL:-0}" == "1" ]]; then
    echo "[setup] Skipping backend module install because LITEMALL_TESTS_SKIP_BACKEND_INSTALL=1"
    return
  fi

  if [[ "$BACKEND_ARTIFACTS_INSTALLED" == "0" ]]; then
    echo "[setup] Installing current litemall backend modules into local Maven repository..."
    (cd "$ROOT" && mvn -q -DskipTests install)
    BACKEND_ARTIFACTS_INSTALLED=1
  fi
}

ensure_npm_deps() {
  local dir="$1"
  local lockfile="$dir/package-lock.json"
  local sentinel_dir="$dir/node_modules/.cache/litemall-tests"
  local sentinel="$sentinel_dir/package-lock.sha256"

  if [[ ! -d "$dir/node_modules" ]]; then
    install_npm_deps "$dir" "$lockfile" "$sentinel_dir" "$sentinel"
    return
  fi

  if [[ -f "$lockfile" ]]; then
    local expected_hash
    expected_hash="$(lockfile_hash "$lockfile")"
    if [[ ! -f "$sentinel" || "$(cat "$sentinel")" != "$expected_hash" ]]; then
      install_npm_deps "$dir" "$lockfile" "$sentinel_dir" "$sentinel"
    fi
  fi
}

install_npm_deps() {
  local dir="$1"
  local lockfile="$2"
  local sentinel_dir="$3"
  local sentinel="$4"

  echo "[setup] Installing npm dependencies in $dir..."
  if [[ -f "$lockfile" ]]; then
    (cd "$dir" && npm ci)
    mkdir -p "$sentinel_dir"
    lockfile_hash "$lockfile" > "$sentinel"
  else
    (cd "$dir" && npm install)
  fi
}

lockfile_hash() {
  if command -v shasum >/dev/null 2>&1; then
    shasum -a 256 "$1" | awk '{print $1}'
  elif command -v sha256sum >/dev/null 2>&1; then
    sha256sum "$1" | awk '{print $1}'
  else
    echo "Neither shasum nor sha256sum is available for package-lock hash checks." >&2
    exit 2
  fi
}

script_checks() {
  (cd "$ROOT" && bash Tests/scripts/run-layered-tests-script-test.sh)
}

backend_ut() {
  ensure_backend_artifacts
  (cd "$ROOT" && mvn -q -f Tests/backend/pom.xml -Dtest='*UnitTest' test)
}

browse_backend_ut() {
  ensure_backend_artifacts
  (cd "$ROOT" && mvn -q -f Tests/backend/pom.xml -Dtest=WxProductBrowsingUnitTest test)
}

contract() {
  ensure_backend_artifacts
  (cd "$ROOT" && mvn -q -f Tests/backend/pom.xml -Dtest='*ContractTest' test)
}

browse_contract() {
  ensure_backend_artifacts
  (cd "$ROOT" && mvn -q -f Tests/backend/pom.xml -Dtest=ProductBrowsingContractTest test)
}

backend_it() {
  ensure_backend_artifacts
  (cd "$ROOT" && mvn -q -f Tests/backend/pom.xml -DskipITs=false verify)
}

backend_coverage() {
  ensure_backend_artifacts
  (cd "$ROOT" && mvn -q -f Tests/backend/pom.xml -Pcoverage -DskipITs=false verify)
  (cd "$ROOT" && mvn -q -f Tests/backend/pom.xml org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy \
    -Dartifact="org.jacoco:org.jacoco.cli:${JACOCO_VERSION}:jar:nodeps" \
    -DoutputDirectory="$ROOT/Tests/backend/target")

  local cli_jar
  cli_jar="$(find "$ROOT/Tests/backend/target" -maxdepth 1 -name 'org.jacoco.cli-*nodeps.jar' -print -quit)"
  if [[ -z "$cli_jar" ]]; then
    echo "Unable to find JaCoCo CLI jar in Tests/backend/target" >&2
    exit 2
  fi

  mkdir -p "$ROOT/Tests/backend/target/site/jacoco"
  java -jar "$cli_jar" report "$ROOT/Tests/backend/target/jacoco.exec" \
    --classfiles "$ROOT/litemall-core/target/classes" \
    --classfiles "$ROOT/litemall-db/target/classes" \
    --classfiles "$ROOT/litemall-wx-api/target/classes" \
    --sourcefiles "$ROOT/litemall-core/src/main/java" \
    --sourcefiles "$ROOT/litemall-db/src/main/java" \
    --sourcefiles "$ROOT/litemall-wx-api/src/main/java" \
    --html "$ROOT/Tests/backend/target/site/jacoco" \
    --xml "$ROOT/Tests/backend/target/site/jacoco/jacoco.xml" \
    --csv "$ROOT/Tests/backend/target/site/jacoco/jacoco.csv"
  echo "[coverage] Backend report: Tests/backend/target/site/jacoco/index.html"
}

frontend_ut() {
  ensure_npm_deps "$ROOT/Tests/frontend"
  (cd "$ROOT/Tests/frontend" && npm run test:ut)
}

frontend_it() {
  ensure_npm_deps "$ROOT/Tests/frontend"
  (cd "$ROOT/Tests/frontend" && npm run test:it)
}

browse_frontend_it() {
  ensure_npm_deps "$ROOT/Tests/frontend"
  (cd "$ROOT/Tests/frontend" && npm run test:browse-it)
}

frontend_coverage() {
  ensure_npm_deps "$ROOT/Tests/frontend"
  (cd "$ROOT/Tests/frontend" && npm run coverage)
  echo "[coverage] Frontend report: Tests/frontend/coverage/index.html"
}

coverage() {
  backend_coverage
  frontend_coverage
}

e2e() {
  ensure_npm_deps "$ROOT/Tests/e2e"
  (cd "$ROOT/Tests/e2e" && npx playwright install chromium && npm test)
}

browse_e2e() {
  ensure_npm_deps "$ROOT/Tests/e2e"
  (cd "$ROOT/Tests/e2e" && npx playwright install chromium && npm test -- tests/product-browsing-flow.spec.ts)
}

browse_fast() {
  browse_backend_ut
  browse_contract
  browse_frontend_it
}

performance_gate() {
  (cd "$ROOT" && ruby Tests/performance/validate-performance-gate.rb)
}

main() {
  case "$LAYER" in
    scripts)
      script_checks
      ;;
    backend-ut)
      backend_ut
      ;;
    browse-backend-ut)
      browse_backend_ut
      ;;
    contract)
      contract
      ;;
    browse-contract)
      browse_contract
      ;;
    backend-it)
      backend_it
      ;;
    backend-coverage)
      backend_coverage
      ;;
    frontend-ut)
      frontend_ut
      ;;
    frontend-it)
      frontend_it
      ;;
    browse-frontend-it)
      browse_frontend_it
      ;;
    frontend-coverage)
      frontend_coverage
      ;;
    coverage)
      coverage
      ;;
    e2e)
      e2e
      ;;
    browse-e2e)
      browse_e2e
      ;;
    performance-gate)
      performance_gate
      ;;
    fast)
      script_checks
      backend_ut
      contract
      frontend_ut
      frontend_it
      ;;
    browse-fast)
      browse_fast
      ;;
    all)
      script_checks
      backend_ut
      contract
      frontend_ut
      frontend_it
      backend_it
      e2e
      ;;
    *)
      echo "Usage: $0 [scripts|backend-ut|browse-backend-ut|contract|browse-contract|backend-it|backend-coverage|frontend-ut|frontend-it|browse-frontend-it|frontend-coverage|coverage|e2e|browse-e2e|performance-gate|fast|browse-fast|all]" >&2
      exit 2
      ;;
  esac
}

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
  main
fi
