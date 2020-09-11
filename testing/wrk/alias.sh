#!/usr/bin/env sh

host='http://127.0.0.1'
# host='http://45.55.50.54'
path='api/alias'

url=${host}/${path}

/usr/local/bin/wrk -t10 -c40 -d30s --latency -s alias.lua ${url}