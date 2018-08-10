package com.ryan.log

class Log2Console implements ILog {

    def TAG = "Log2Console"


    @Override
    void d(String tag = TAG, String content) {
        println ""
        println "$tag  :\n$content"
    }

    @Override
    void e(String tag = TAG, String content) {
        System.err.println("$tag   | $content")
    }
}