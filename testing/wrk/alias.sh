#!/usr/bin/env sh

host='http://localhost'
# host='http://45.55.50.54'
path='api/v1/shortenUrl'

url=${host}/${path}

/usr/local/bin/wrk -t10 -c40 -d30s --latency -s alias.lua ${url}
