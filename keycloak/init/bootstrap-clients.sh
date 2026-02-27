#!/usr/bin/env bash
set -euo pipefail

KC_SERVER="${KEYCLOAK_BASE_URL_INTERNAL:-http://keycloak:8080}"
REALM="${KEYCLOAK_REALM:-cse-sso}"
AUTH_CLIENT_ID="${KEYCLOAK_CLIENT_ID:-cse-sso-server}"
AUTH_CLIENT_SECRET="${KEYCLOAK_CLIENT_SECRET:?KEYCLOAK_CLIENT_SECRET is required}"
ADMIN_CLIENT_ID="${KEYCLOAK_ADMIN_CLIENT_ID:-cse-sso-admin}"
ADMIN_CLIENT_SECRET="${KEYCLOAK_ADMIN_CLIENT_SECRET:?KEYCLOAK_ADMIN_CLIENT_SECRET is required}"
AUTH_SERVER_PORT="${AUTH_SERVER_PORT:-11111}"
AUTH_CLIENT_BASE_URL="${AUTH_CLIENT_BASE_URL:-http://localhost:${AUTH_SERVER_PORT}}"
AUTH_CLIENT_REDIRECT_URIS="${AUTH_CLIENT_REDIRECT_URIS:-${AUTH_CLIENT_BASE_URL}/login/oauth2/code/keycloak}"
AUTH_CLIENT_WEB_ORIGINS="${AUTH_CLIENT_WEB_ORIGINS:-${AUTH_CLIENT_BASE_URL}}"
AUTH_CLIENT_POST_LOGOUT_REDIRECT_URIS="${AUTH_CLIENT_POST_LOGOUT_REDIRECT_URIS:-${AUTH_CLIENT_BASE_URL}/*##${AUTH_CLIENT_BASE_URL}}"
GOOGLE_IDP_ALIAS="${GOOGLE_IDP_ALIAS:-google}"
GOOGLE_IDP_CLIENT_ID="${GOOGLE_IDP_CLIENT_ID:-}"
GOOGLE_IDP_CLIENT_SECRET="${GOOGLE_IDP_CLIENT_SECRET:-}"

/opt/keycloak/bin/kcadm.sh config credentials \
  --server "$KC_SERVER" \
  --realm master \
  --user "${KC_BOOTSTRAP_ADMIN_USERNAME}" \
  --password "${KC_BOOTSTRAP_ADMIN_PASSWORD}"

get_client_uuid() {
  local client_id="$1"
  /opt/keycloak/bin/kcadm.sh get clients -r "$REALM" -q "clientId=${client_id}" \
    | sed -n 's/.*"id" : "\([^"]*\)".*/\1/p' \
    | head -n 1
}

csv_to_json_array() {
  local csv="$1"
  local json="["
  local sep=""
  local item
  IFS=',' read -r -a items <<< "$csv"
  for item in "${items[@]}"; do
    item="${item#"${item%%[![:space:]]*}"}"
    item="${item%"${item##*[![:space:]]}"}"
    if [ -n "$item" ]; then
      json+="${sep}\"${item}\""
      sep=","
    fi
  done
  json+="]"
  echo "$json"
}

AUTH_CLIENT_UUID="$(get_client_uuid "$AUTH_CLIENT_ID")"
if [ -z "$AUTH_CLIENT_UUID" ]; then
  echo "Client '${AUTH_CLIENT_ID}' not found in realm '${REALM}'. Check imported realm JSON." >&2
  exit 1
fi

AUTH_CLIENT_REDIRECT_URIS_JSON="$(csv_to_json_array "$AUTH_CLIENT_REDIRECT_URIS")"
AUTH_CLIENT_WEB_ORIGINS_JSON="$(csv_to_json_array "$AUTH_CLIENT_WEB_ORIGINS")"

/opt/keycloak/bin/kcadm.sh update "clients/${AUTH_CLIENT_UUID}" -r "$REALM" \
  -s "secret=${AUTH_CLIENT_SECRET}" \
  -s "redirectUris=${AUTH_CLIENT_REDIRECT_URIS_JSON}" \
  -s "webOrigins=${AUTH_CLIENT_WEB_ORIGINS_JSON}" \
  -s "attributes.\"post.logout.redirect.uris\"=${AUTH_CLIENT_POST_LOGOUT_REDIRECT_URIS}" >/dev/null

ADMIN_CLIENT_UUID="$(get_client_uuid "$ADMIN_CLIENT_ID")"
if [ -z "$ADMIN_CLIENT_UUID" ]; then
  echo "Client '${ADMIN_CLIENT_ID}' not found in realm '${REALM}'. Check imported realm JSON." >&2
  exit 1
fi

/opt/keycloak/bin/kcadm.sh update "clients/${ADMIN_CLIENT_UUID}" -r "$REALM" \
  -s "secret=${ADMIN_CLIENT_SECRET}" >/dev/null

ADMIN_SERVICE_ACCOUNT_USER_ID="$(
  /opt/keycloak/bin/kcadm.sh get "clients/${ADMIN_CLIENT_UUID}/service-account-user" -r "$REALM" \
    | sed -n 's/.*"id" : "\([^"]*\)".*/\1/p' \
    | head -n 1
)"

if [ -n "$ADMIN_SERVICE_ACCOUNT_USER_ID" ]; then
  /opt/keycloak/bin/kcadm.sh add-roles -r "$REALM" \
    --uid "$ADMIN_SERVICE_ACCOUNT_USER_ID" \
    --cclientid realm-management \
    --rolename realm-admin \
    --rolename manage-users \
    --rolename query-users >/dev/null
fi

if [ -n "$GOOGLE_IDP_CLIENT_ID" ] && [ -n "$GOOGLE_IDP_CLIENT_SECRET" ]; then
  if /opt/keycloak/bin/kcadm.sh get "identity-provider/instances/${GOOGLE_IDP_ALIAS}" -r "$REALM" >/dev/null 2>&1; then
    /opt/keycloak/bin/kcadm.sh update "identity-provider/instances/${GOOGLE_IDP_ALIAS}" -r "$REALM" \
      -s enabled=true \
      -s "config.clientId=${GOOGLE_IDP_CLIENT_ID}" \
      -s "config.clientSecret=${GOOGLE_IDP_CLIENT_SECRET}"
  else
    /opt/keycloak/bin/kcadm.sh create identity-provider/instances -r "$REALM" \
      -s "alias=${GOOGLE_IDP_ALIAS}" \
      -s providerId=google \
      -s enabled=true \
      -s "config.clientId=${GOOGLE_IDP_CLIENT_ID}" \
      -s "config.clientSecret=${GOOGLE_IDP_CLIENT_SECRET}"
  fi
elif [ -n "$GOOGLE_IDP_CLIENT_ID" ] || [ -n "$GOOGLE_IDP_CLIENT_SECRET" ]; then
  echo "GOOGLE_IDP_CLIENT_ID and GOOGLE_IDP_CLIENT_SECRET must be provided together." >&2
  exit 1
else
  echo "Google IdP credentials are not set. Skipping Google IdP sync."
fi

echo "Keycloak bootstrap completed for realm '${REALM}'."
