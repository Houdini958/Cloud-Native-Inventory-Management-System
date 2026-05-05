#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/opt/inventory-management-system}"
REPO_URL="${REPO_URL:-}"

if [[ -z "${REPO_URL}" ]]; then
  echo "Set REPO_URL env variable to your git repository URL."
  exit 1
fi

sudo apt update
sudo apt install -y docker.io docker-compose-plugin git
sudo systemctl enable docker
sudo systemctl start docker

if [[ ! -d "${APP_DIR}" ]]; then
  sudo mkdir -p "${APP_DIR}"
  sudo chown -R "$USER":"$USER" "${APP_DIR}"
  git clone "${REPO_URL}" "${APP_DIR}"
fi

cd "${APP_DIR}"
git pull --rebase
docker compose down
docker compose up --build -d
docker image prune -f

echo "Deployment completed."
