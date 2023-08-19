const {dest, series, src, parallel, watch} = require("gulp");
const {createProject} = require("gulp-typescript");
const browserify = require("browserify");
const tsify = require("tsify");
const source = require('vinyl-source-stream');
const inject = require('gulp-inject');
const clean = require('gulp-clean');
const less = require('gulp-less');
const cssmin = require('gulp-minify-css');
const uglify = require('gulp-uglify');
const buffer = require('vinyl-buffer');
const rename = require('gulp-rename');

let debug = false;

let PATH_TEMP = '../web/public/kindle'
// let PATH_TEMP = '../target/classes/kindle'

function cleanDir(cb) {
    return src(debug ? [PATH_TEMP + '/*'] : ['dist/*', PATH_TEMP + '/*']).pipe(clean({force: true}));
}

function buildLess(cb) {
    return src('src/style.less')
        .pipe(less())
        .pipe(cssmin())
        .pipe(dest(PATH_TEMP));
}

function buildTs(cb) {
    let a = browserify({
        basedir: '.',
        debug: debug,
        entries: ['src/main.ts'],
        cache: {},
        packageCache: {}
    })
        .plugin(tsify)
        .bundle()
        .pipe(source('bundle.js'))
        .pipe(buffer());
    if (!debug) {
        a = a.pipe(uglify());
    }
    // .pipe(uglify())
    return a.pipe(dest(PATH_TEMP));
}

function collect(cb) {
    var target = src('./src/index.html');
    var sources = src([PATH_TEMP + '/**/*.js', PATH_TEMP + '/**/*.css'], {read: true});

    return target.pipe(inject(sources, {
        transform: function (filePath, file) {
            if (filePath.slice(-3) === '.js') {
                return '<script>' + file.contents.toString('utf8') + '</script>';
            }
            if (filePath.slice(-4) === '.css') {
                return '<style>' + file.contents.toString('utf8') + '</style>';
            }
            return file.contents.toString('utf8');
        }
    }))
        .pipe(rename('index.html'))
        .pipe(dest(PATH_TEMP));
}

const task = series(cleanDir, parallel(buildTs, buildLess), collect);

function setFlag(cb) {
    debug = true;
    watch('src', task);
    task();
    cb();
}

function setProd(cb) {
    task()
    cb()
}

exports.default = task;
exports.debug = setFlag;
exports.prod = setProd;
