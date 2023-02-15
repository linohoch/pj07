const webpack = require('webpack');
const path = require('path');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports={

    entry: {
        main: [
            './src/index.js',
            './src/pageload.js'
        ],
        write: [
            './src/write.js'
        ],
        sign: [
            './src/auth/sign.js'
        ]


    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: '[name].bundle.js',
        chunkFilename: "[name].bundle.js",
        publicPath: "/resources/dist/",
        // libraryTarget: 'var',
        // library: '[name]'
    },
    resolve: {
        modules: [path.resolve('.'), '.', "node_modules"],
        extensions: [".js", ".json",".ts"],
        alias: {
            /** import시 리소스 경로 잡아주는 알리아스 */
            '@': path.join(__dirname, 'js'),
        }
    },
    plugins: [
        new HtmlWebpackPlugin({
            title: 'Hot Module Replacement',
        }),
        // new webpack.HotModuleReplacementPlugin(),
    ],
    target: 'web',

    module:{
        rules:[
            {
                test: /\.js$/,
                exclude: /(node_modules|pages)/,
                use:{
                    loader:'babel-loader',
                },
            },
            {
                test: /\.css$/,
                use: [
                    { loader: MiniCssExtractPlugin.loader },
                    {
                        loader: 'css-loader',
                        options: { import:true },
                    },
                ],
            },
            {
                test: /\.png$/,
                type: 'asset/resource',
            },
            {
                test: /\.html$/,
                loader: 'html-loader'
            },
            {
                test: /\.art$/,
                loader: "art-template-loader",
                options: {
                    // art-template options (if necessary)
                    // @see https://github.com/aui/art-template
                }
            }
        ],
    },


    mode: 'development',

    devtool: 'eval',

    devServer: {
        hot: true,
        liveReload: false,
        historyApiFallback: true,
        compress: true,
        static: {
            directory: path.resolve(__dirname, 'dist'),
        },
        host: "0.0.0.0",
        port: 80,
        proxy: {
            "**": "http://localhost:8080/"
        }
    },





};