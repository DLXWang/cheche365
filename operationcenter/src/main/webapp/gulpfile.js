'use strict'

var gulp = require("gulp");
var babel = require("gulp-babel");
var webpack = require("webpack");
// var WebpackDevServer = require('webpack-dev-server')dd
var gutil     = require('gulp-util');
var clean = require('gulp-clean');
var connect = require('gulp-connect');
var webpackConfig = require('./webpack.config.js');
var livereload = require('gulp-livereload');

gulp.task('clean', function(){
    return gulp.src('build/', {read: false})
        .pipe(clean());
});

gulp.task("default",['clean', 'webpack'], function () {
  // return gulp.src("client/test1/*.js")
  //   .pipe(babel())
  //   .pipe(gulp.dest("client/build"));
  gulp.start(['connect', 'watch']);
});

gulp.task('webpack', function(){
	webpack(webpackConfig, function(err, stats){
		if(err) throw new gutil.PluginError("webpack", err);
        gutil.log("[webpack]", stats.toString({
            // output options
        }));
        // done();
	})
});


gulp.task("connect", function(){
  connect.server({
      port: 8000,
      livereload: true
    });
    // notify({ message: 'Styles task complete' });
})

gulp.task("watch", function(){
  livereload.listen();
  gulp.watch('components/**/*.js', ['webpack']).on('change', livereload.changed);
  // gulp.watch('client/**/*.css', ['styles']).on('change', livereload.changed);
  // gulp.watch('client/**/*.html', ['html']).on('change', livereload.changed);
  // livereload();
  // });
});
