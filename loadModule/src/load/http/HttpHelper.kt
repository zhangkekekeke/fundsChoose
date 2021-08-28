package load.http

import load.util.isEmpty
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection

class HttpHelper {
    var httpCache = HttpCache()
    fun httpGet(path: String, headerMap: Map<String, String>?): String? {
        val response = httpCache.check(path)
        if (!isEmpty(response))
            return response//有缓存 返回

        val url = URL(path)
        //获取URL连接，open方法返回一个URLConnection类的对象
        val conn = url.openConnection()
        //设置请求头
        if (headerMap != null) {
            for (entry: Map.Entry<String, String> in headerMap) {
                conn.addRequestProperty(entry.key, entry.value)
//            conn.addRequestProperty("Cookie", "PHPSESSID=9u4lc1m0cp82j6fbjrq92rv7e1; ticket=9a7f1b0c86d568ab7da1e40173b55693; name=vskrss; code=987456; qd=1");
            }
        }

        val newRepsones = getHttpRespone(conn)
        //保存缓存
        httpCache.save(path, newRepsones)
        return newRepsones
    }

    fun httpPost(path: String, headerMap: Map<String, String>?): String? {
        val respones = httpCache.check(path)
        if (!isEmpty(respones)) {
            println("Http请求，已使用缓存")
            return respones//有缓存 返回
        }

        val url = URL(path)
        //获取URL连接，open方法返回一个URLConnection类的对象
        val conn = url.openConnection() as HttpURLConnection
        //设置请求头
        if (headerMap != null) {
            for (entry: Map.Entry<String, String> in headerMap) {
                conn.addRequestProperty(entry.key, entry.value)
//            conn.addRequestProperty("Cookie", "PHPSESSID=9u4lc1m0cp82j6fbjrq92rv7e1; ticket=9a7f1b0c86d568ab7da1e40173b55693; name=vskrss; code=987456; qd=1");
            }
        }
        //设置请求方式为POST
        conn.setRequestMethod("POST");
        val newRepsones = getHttpRespone(conn)
        //保存缓存
        httpCache.save(path, newRepsones)

        return newRepsones
    }

    private fun getHttpRespone(conn: URLConnection): String? {
        System.out.println("正在请求网络：" + conn.url)
        try {
            //从连接获取输入流，请求的输入也就是对请求的输入，即是相应，
            val `in` = conn.getInputStream()

            val bufferedInputStream = BufferedInputStream(`in`)
            var byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
            val b = ByteArray(1024)
            var length: Int
            while (bufferedInputStream.read(b).also {
                    length = it
                } > 0) {
                byteArrayOutputStream.write(b, 0, length)
            }

            return byteArrayOutputStream.toString("utf-8")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}