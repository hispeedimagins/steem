package com.insteem.ipfreely.steem.HelperClasses

import java.net.URLEncoder

//implementation of  proxify class from steemit
class ProxifyUrl {
    companion object {
        val rProxyDomain = "^http(s)?://steemit(dev|stage)?images.com/"
        val rProxyDomainsDimensions = "http(s)?://steemit(dev|stage)?images.com/([0-9]+x[0-9]+)/"
        val NATURAL_SIZE = "0x0/"
        //use steemitimages insted of dev for the proxy url
        val img_proxy_prefix = "https://steemitimages.com/"


        //returns the url which will be rendered
        //by the dimension or zero
        fun proxyurl(url:String,dimensions:Any = false) : String{
            var reg = rProxyDomainsDimensions.toRegex()
            var regm = reg.findAll(url)
            var respUrl = url
            if(regm.any()){
                var lastproxy = regm.last()
                respUrl = url.substring(url.lastIndexOf(lastproxy.value) + lastproxy.value.length)
            }
            if(!img_proxy_prefix.isNullOrEmpty()){
                var dims = ""
                if(dimensions is String)
                    dims = "$dimensions/"
                else if(dimensions is Boolean)
                    dims = if(regm.any()) "([0-9]+x[0-9]+)/".toRegex().find(regm.first().value)?.groupValues?.first()!! else NATURAL_SIZE

                if (NATURAL_SIZE !== dims || !rProxyDomain.toRegex().matches(respUrl)) {
                    return img_proxy_prefix + dims + URLEncoder.encode(respUrl, "utf-8")
                }

            }
            return respUrl
        }
    }
}