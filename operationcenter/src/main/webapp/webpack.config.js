var path = require("path");
var webpack = require('webpack');
var node_modules_dir = path.resolve(__dirname, 'node_modules');

var config = {
    devtool:false,
    entry: {
        app : path.resolve(__dirname, 'components/app.js'),
    },
    output: {
        path: path.resolve(__dirname, 'buildx/'),
        filename: "[name].js"  
        ,publicPath: "/build/" //网站运行时的访问路径 
    },
    module: {
        loaders: [
            { 
                test: /\.js?$/, 
                loader: 'babel-loader',
                exclude: [node_modules_dir]
            },            
        ]
    },
    resolve: {
        extensions: ['',  '.js', '.json', '.jsx']
    },
    plugins: [  //bootstrap dependency
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery",
            "window.jQuery": "jquery"
        })
    ]
};
module.exports = config;
