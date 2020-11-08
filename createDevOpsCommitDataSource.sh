#!/bin/bash

export DEVOPS_SERVER_URL=https://cddirector.io
export DEVOPS_TENANT_ID="17827b55-3a42-403f-b1f8-9655d7e625bb"
export DEVOPS_API_KEY="eyJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6InJhbGx5MS5yYWxseWRldkBnbWFpbC5jb20iLCJ0ZW5hbnRJZCI6IjE3ODI3YjU1LTNhNDItNDAzZi1iMWY4LTk2NTVkN2U2MjViYiIsInVzZXJJZCI6MSwianRpIjoiMTM1NmQ3YzItMzdhYi00ZTRlLTk1ZWItYTJmZGVlMjgzMzhlIiwiZXhwIjoxNjEyNjA4MjgwfQ.WUVAR0J2lPG4hiOmx486tEmMw_zkksFNqM6NbEtKXe1pduJXSBNbZXdcgFVcuGPz_3qqaYYVs6qqJWB1AT1e6g"


export DIL_MANIFEST_FILENAME=/tmp/dil/saas/github/dil.json

curl -s -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $DEVOPS_API_KEY" -H "Accept: application/json" -d @$DIL_MANIFEST_FILENAME "$DEVOPS_SERVER_URL/cdd/administration/$DEVOPS_TENANT_ID/v1/dsl-manifests"
