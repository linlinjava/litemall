#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

NPM_CALLS_FILE="$TMP_DIR/npm-calls.log"
NPX_CALLS_FILE="$TMP_DIR/npx-calls.log"
MVN_CALLS_FILE="$TMP_DIR/mvn-calls.log"
JAVA_CALLS_FILE="$TMP_DIR/java-calls.log"
export NPM_CALLS_FILE
export NPX_CALLS_FILE
export MVN_CALLS_FILE
export JAVA_CALLS_FILE

mkdir -p "$TMP_DIR/bin"
cat > "$TMP_DIR/bin/npm" <<'NPM'
#!/usr/bin/env bash
set -euo pipefail
echo "$*" >> "$NPM_CALLS_FILE"
case "${1:-}" in
  ci|install)
    mkdir -p node_modules
    ;;
  run|test)
    ;;
  *)
    echo "unexpected npm command: $*" >&2
    exit 2
    ;;
esac
NPM
chmod +x "$TMP_DIR/bin/npm"
cat > "$TMP_DIR/bin/npx" <<'NPX'
#!/usr/bin/env bash
set -euo pipefail
echo "$*" >> "$NPX_CALLS_FILE"
NPX
chmod +x "$TMP_DIR/bin/npx"
cat > "$TMP_DIR/bin/mvn" <<'MVN'
#!/usr/bin/env bash
set -euo pipefail
echo "$*" >> "$MVN_CALLS_FILE"
mkdir -p Tests/backend/target
touch Tests/backend/target/jacoco.exec
for arg in "$@"; do
  case "$arg" in
    -DoutputDirectory=*)
      output_dir="${arg#-DoutputDirectory=}"
      mkdir -p "$output_dir"
      touch "$output_dir/org.jacoco.cli-0.8.13-nodeps.jar"
      ;;
  esac
done
MVN
chmod +x "$TMP_DIR/bin/mvn"
cat > "$TMP_DIR/bin/java" <<'JAVA'
#!/usr/bin/env bash
set -euo pipefail
echo "$*" >> "$JAVA_CALLS_FILE"
JAVA
chmod +x "$TMP_DIR/bin/java"
export PATH="$TMP_DIR/bin:$PATH"

source "$SCRIPT_DIR/run-layered-tests.sh"
export PATH="$TMP_DIR/bin:$PATH"

fixture="$TMP_DIR/npm-project"
mkdir -p "$fixture/node_modules"
cat > "$fixture/package.json" <<'JSON'
{
  "name": "npm-deps-fixture",
  "private": true,
  "version": "1.0.0"
}
JSON
cat > "$fixture/package-lock.json" <<'JSON'
{
  "name": "npm-deps-fixture",
  "lockfileVersion": 3,
  "packages": {
    "": {
      "name": "npm-deps-fixture",
      "version": "1.0.0"
    }
  }
}
JSON

ensure_npm_deps "$fixture"
grep -qx "ci" "$NPM_CALLS_FILE"

: > "$NPM_CALLS_FILE"
ensure_npm_deps "$fixture"
[[ ! -s "$NPM_CALLS_FILE" ]]

cat > "$fixture/package-lock.json" <<'JSON'
{
  "name": "npm-deps-fixture",
  "lockfileVersion": 3,
  "packages": {
    "": {
      "name": "npm-deps-fixture",
      "version": "1.0.1"
    }
  }
}
JSON

ensure_npm_deps "$fixture"
grep -qx "ci" "$NPM_CALLS_FILE"

performance_gate

ROOT="$TMP_DIR/project"
mkdir -p "$ROOT/Tests/frontend/node_modules" "$ROOT/Tests/frontend/node_modules/.cache/litemall-tests" \
  "$ROOT/Tests/e2e/node_modules" "$ROOT/Tests/e2e/node_modules/.cache/litemall-tests"
cat > "$ROOT/Tests/frontend/package-lock.json" <<'JSON'
{
  "name": "frontend-coverage-fixture",
  "lockfileVersion": 3,
  "packages": {
    "": {
      "name": "frontend-coverage-fixture",
      "version": "1.0.0"
    }
  }
}
JSON
lockfile_hash "$ROOT/Tests/frontend/package-lock.json" > "$ROOT/Tests/frontend/node_modules/.cache/litemall-tests/package-lock.sha256"
cat > "$ROOT/Tests/e2e/package-lock.json" <<'JSON'
{
  "name": "e2e-fixture",
  "lockfileVersion": 3,
  "packages": {
    "": {
      "name": "e2e-fixture",
      "version": "1.0.0"
    }
  }
}
JSON
lockfile_hash "$ROOT/Tests/e2e/package-lock.json" > "$ROOT/Tests/e2e/node_modules/.cache/litemall-tests/package-lock.sha256"

: > "$NPM_CALLS_FILE"
: > "$MVN_CALLS_FILE"
LAYER=coverage
main
grep -q -- "-Pcoverage" "$MVN_CALLS_FILE"
grep -qx "run coverage" "$NPM_CALLS_FILE"
grep -q -- "jacoco.exec" "$JAVA_CALLS_FILE"

export LITEMALL_TESTS_SKIP_BACKEND_INSTALL=1

: > "$NPM_CALLS_FILE"
: > "$NPX_CALLS_FILE"
: > "$MVN_CALLS_FILE"
LAYER=browse-backend-ut
main
grep -q -- "-Dtest=WxProductBrowsingUnitTest" "$MVN_CALLS_FILE"

: > "$MVN_CALLS_FILE"
LAYER=browse-contract
main
grep -q -- "-Dtest=ProductBrowsingContractTest" "$MVN_CALLS_FILE"

: > "$NPM_CALLS_FILE"
LAYER=browse-frontend-it
main
grep -qx "run test:browse-it" "$NPM_CALLS_FILE"

: > "$NPM_CALLS_FILE"
: > "$NPX_CALLS_FILE"
LAYER=browse-e2e
main
grep -qx "playwright install chromium" "$NPX_CALLS_FILE"
grep -qx "test -- tests/product-browsing-flow.spec.ts" "$NPM_CALLS_FILE"

: > "$NPM_CALLS_FILE"
: > "$MVN_CALLS_FILE"
LAYER=browse-fast
main
grep -q -- "-Dtest=WxProductBrowsingUnitTest" "$MVN_CALLS_FILE"
grep -q -- "-Dtest=ProductBrowsingContractTest" "$MVN_CALLS_FILE"
grep -qx "run test:browse-it" "$NPM_CALLS_FILE"

echo "[scripts] run-layered-tests npm dependency guard passed"
