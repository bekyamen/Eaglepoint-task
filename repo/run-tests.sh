#!/usr/bin/env sh
set -eu

echo "==> Running backend tests (Maven)"
mvn -B test

echo "==> Running frontend tests (Angular)"
npm ci
npm run test -- --watch=false --browsers=ChromeHeadlessNoSandbox

echo "==> All tests passed"
