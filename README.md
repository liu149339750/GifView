# GifView
一个gif解码的示例

gif采用LZW算法，里面的算法处理的不够好，解码速度不够快，不建议使用。gif解析有个问题就是图片如果足够大，解析出来的图片存在内存，那么
可能导致OOM，这个没把图片存内存，存成了文件，最后导致cpu占用率过高。
示例中的jni部分请无视，其实用jni速度可能会更慢，纯加深一下jni映像加上的。
这个目前的价值我认为只剩下参照着学习gif的编解码了。
