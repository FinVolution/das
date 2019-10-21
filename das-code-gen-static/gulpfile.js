var gulp = require('gulp');
var path = require('path');
var gutil = require("gulp-util");
var open = require('gulp-open');
var webpackServer = require('./system/webpack-dev.config');
var webpackConfig = require('./system/webpack.config');
var less = require('gulp-less');
var includer = require('gulp-htmlincluder');
var runSequence = require('run-sequence').use(gulp);
var clean = require('gulp-rimraf');
var webpack = require("webpack");
var glob = require("glob");
var config = require('./system/config/base.config');
var internalIP = require('internal-ip');
var flatten = require('gulp-flatten');
var imagemin = require('gulp-imagemin');
var smushit = require('gulp-smushit');
var pngquant = require('imagemin-pngquant');

var devPort = config.devPort;

gulp.task('open', function () {
    gulp.src(__filename)
        .pipe(open({uri: "http://" + (internalIP.v4() || '127.0.0.1') + ":" + devPort + config.defaultStartPage}));
});

gulp.task('hot', function (callback) {
    webpackServer();

});

gulp.task('min-webpack', function (done) {
    var wbpk = Object.create(webpackConfig);
    wbpk.output.filename = '[name].min.js';
    wbpk.plugins.push(new webpack.optimize.UglifyJsPlugin());

    webpack(wbpk).run(function (err, stats) {
        if (err) throw new gutil.PluginError("min-webpack", err);
        gutil.log("[min-webpack]", stats.toString({}));
        done();
    });
});

gulp.task('webpack', function (callback) {
    webpack(
        webpackConfig, function (err, stats) {
            if (err) throw new gutil.PluginError("webpack", err);
            gutil.log("[webpack]", stats.toString({
                // output options
            }));
            callback();
        });
});

gulp.task('html-includer', function () {
    return gulp.src(config.html + '/**/*.html')
        .pipe(includer())
        .on('error', console.error)
        .pipe(gulp.dest('html'));
});


gulp.task('clean', function () {
    gulp.src([
        config.output
    ], {read: false})
        .pipe(clean());
});

gulp.task('html', function () {
    return gulp.src([config.html + '/**/*.html'])
        .pipe(flatten())
        .pipe(gulp.dest(config.output));
});
gulp.task('default', function () {
    runSequence('clean', 'webpack', 'html');
});

gulp.task('imagemin', function () {
    return gulp.src('src/*')
        .pipe(imagemin({
            progressive: true,
            use: [pngquant()]
        }))
        .pipe(gulp.dest('imagemin-dist'));
});

gulp.task('smushit', function () {
    return gulp.src('src/*')
        .pipe(smushit({
            verbose: true
        }))
        .pipe(gulp.dest('smushit-dist'));
});
gulp.task('dev', ['hot', 'open']);
