const { override, addWebpackPlugin, addWebpackModuleRule } = require('customize-cra');
const webpack = require('webpack');

module.exports = override(
    (config) => {
        // Add fallbacks for Node.js core modules
        config.resolve.fallback = {
            ...config.resolve.fallback,
            "process": false,
            "stream": require.resolve("stream-browserify"),
            "util": require.resolve("util/"),
            "buffer": require.resolve("buffer/"),
            "assert": require.resolve("assert/"),
            "crypto": require.resolve("crypto-browserify"),
            "http": require.resolve("stream-http"),
            "https": require.resolve("https-browserify"),
            "os": require.resolve("os-browserify/browser"),
            "url": require.resolve("url/"),
            "path": require.resolve("path-browserify"),
            "fs": false,
            "net": false,
            "tls": false,
            "child_process": false,
            "zlib": false
        };

        // Add plugins
        config.plugins = [
            ...config.plugins,
            new webpack.ProvidePlugin({
                process: 'process/browser.js',
                Buffer: ['buffer', 'Buffer']
            })
        ];

        // Add alias for process
        config.resolve.alias = {
            ...config.resolve.alias,
            'process': 'process/browser.js'
        };

        // Configure proxy
        config.devServer = {
            ...config.devServer,
            proxy: {
                '/api': {
                    target: 'http://localhost:8080',
                    changeOrigin: true,
                    secure: false,
                    logLevel: 'debug',
                    onProxyReq: (proxyReq, req, res) => {
                        console.log('Proxying request:', req.method, req.url);
                    },
                    onProxyRes: (proxyRes, req, res) => {
                        console.log('Received response:', proxyRes.statusCode, req.url);
                    },
                    onError: (err, req, res) => {
                        console.error('Proxy error:', err);
                        res.writeHead(500, {
                            'Content-Type': 'text/plain',
                        });
                        res.end('Proxy error: ' + err.message);
                    }
                }
            },
            historyApiFallback: {
                disableDotRule: true,
                rewrites: [
                    { from: /^\/api/, to: '/api' }
                ]
            },
            headers: {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, PATCH, OPTIONS',
                'Access-Control-Allow-Headers': 'X-Requested-With, content-type, Authorization'
            }
        };

        return config;
    }
); 