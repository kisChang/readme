const path = require("path");
const CompressionWebpackPlugin = require("compression-webpack-plugin");
const productionGzipExtensions = ["js", "css"];

function resolve(dir) {
  return path.join(__dirname, dir);
}


module.exports = {
  publicPath: "/",
  // CDN配置，暂未启用
  // publicPath: process.env.NODE_ENV === "production" ? "//qncdn.babyceo.cn/" : "/",
  assetsDir: 'static',
  outputDir: 'dist',
  productionSourceMap: false,
  transpileDependencies: [],

  //开发支持
  devServer: {
    port: 80,
    disableHostCheck: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        ws: true,
      },
    },
  },

  configureWebpack: config => {
    config.resolve.extensions =
      ['.tsx', '.ts', '.mjs', '.js', '.jsx', '.vue', '.json', '.wasm']

    if (process.env.NODE_ENV === "production"){
      return {
        plugins: [
          // 代码压缩
          new CompressionWebpackPlugin({
            // filename: '[path].gz[query]',
            algorithm: "gzip",
            test: new RegExp("\\.(" + productionGzipExtensions.join("|") + ")$"),
            threshold: 10240, //对超过10k的数据进行压缩
            minRatio: 0.6 // 压缩比例，值为0 ~ 1
          }),
        ]
      };
    }
  },
  chainWebpack: config => {
    // 生产版本增加代码混淆
    if (process.env.NODE_ENV === "production") {
      config.optimization.minimizer('js')
        .use(require.resolve('terser-webpack-plugin'), [{
          terserOptions: {
            mangle: true, // 混淆，默认也是开的，mangle也是可以配置很多选项的，具体看后面的链接
            format: {
              comments: false,
            },
            compress: {
              drop_console: true,//传true就是干掉所有的console.*这些函数的调用.
              drop_debugger: true, //干掉那些debugger;
              pure_funcs: ['console.log'] // 如果你要干掉特定的函数比如console.info ，又想删掉后保留其参数中的副作用，那用pure_funcs来处理
            }
          },
          extractComments: false,
        }])
    } else {
      // disable optimization during tests to speed things up
      config.optimization.minimize(false)
    }

    config.resolve.alias
      .set("@", resolve("src"))

    config.module
      .rule('ts')
      .test(/\.tsx?$/)
      .exclude
        .add(resolve('node_modules'))
        .end()
      .use('cache-loader')
        .loader('cache-loader')
        .options({
          cacheDirectory: resolve('node_modules/.cache/ts-loader')
        })
        .end()
      .use('babel-loader')
        .loader('babel-loader')
        .end()
      .use('ts-loader')
        .loader('ts-loader')
        .options({
          transpileOnly: true, // 关闭类型检查，即只进行转译(类型检查交给webpack插件(fork-ts-checker-webpack-plugin)在另一个进程中进行,这就是所谓的多进程方案,如果设置transpileOnly为false, 则编译和类型检查全部由ts-loader来做, 这就是单进程方案.显然多进程方案速度更快)
          appendTsSuffixTo: ['\\.vue$'],
          happyPackMode: false
        })
        .end()
  }
};
