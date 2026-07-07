#!/bin/sh
set -eu

API_URL_VALUE="${API_URL:-/api}"

cat <<EOF >/usr/share/nginx/html/env.js
window.__env = {
  API_URL: "${API_URL_VALUE}"
};
EOF
