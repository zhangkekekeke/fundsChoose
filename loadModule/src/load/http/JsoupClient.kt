package load.http

import load.util.isEmpty
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * java解析 html数据
 */
class JsoupClient {
    val httpCache = HttpCache()

    fun getJsoup(url: String): Document {
        val doc = httpCache.check(url)
        if (!isEmpty(doc)) {
//            System.out.println("Jsoup请求，已使用缓存")
            return Jsoup.parse(doc)//有缓存 返回
        }

        val document = Jsoup.connect(url).get();
        httpCache.save(url, document.toString())
        return document
    }
}