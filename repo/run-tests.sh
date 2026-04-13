#!/usr/bin/env sh
set -eu

echo "==> Running backend tests"
if command -v mvn >/dev/null 2>&1; then
  echo "Using local Maven"
  mvn -B test
else
  echo "Maven not found, using Docker test profile fallback"
  docker-compose --profile test up --build --abort-on-container-exit --exit-code-from tests
  docker-compose --profile test down --volumes --remove-orphans
fi

echo "==> Running frontend tests (Angular)"
npm ci
npm run test -- --watch=false --browsers=ChromeHeadlessNoSandbox

echo "==> All tests passed"
