#!/usr/bin/env sh

host='http://127.0.0.1'
#host='http://45.55.50.54'
path='1'

url=${host}/${path}

/usr/local/bin/wrk -t10 -c100 -d30s --latency -s resolution.lua ${url}
