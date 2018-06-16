package com.steemapp.lokisveil.steemapp.HelperClasses


//implementation of steemit's link class
class Links {

    companion object {
        var urlChar = "[^\\s\"<>\\]\\[\\(\\)]"
        var urlCharEnd = urlChar.replace("/]$/".toRegex(), ".,']"); // insert bad chars to end on
        var imagePath ="(?:(?:\\.(?:tiff?|jpe?g|gif|png|svg|ico)|ipfs/[a-z\\d]{40,}))"
        var domainPath = "(?:[-a-zA-Z0-9\\._]*[-a-zA-Z0-9])";
        var urlChars = "(?:$urlChar*$urlCharEnd)?"
        var youTubeId = "/(?:(?:youtube.com/watch?v=)|(?:youtu.be/)|(?:youtube.com/embed/))([A-Za-z0-9_-]+)/i"
        var vimeo = "/https?://(?:vimeo.com/|player.vimeo.com/video/)([0-9]+)/*/"
        var vimeoId = " /(?:vimeo.com/|player.vimeo.com/video/)([0-9]+)/"
        var ipfsPrefix = "/(https?://.*)?/ipfs/i"

        fun urlSet(domain:String = domainPath,path:String? = null):String{
            return "([^](=\"/'])(https?://$domain(?::\\d{2,5})?(?:[/\\?#]$urlChars${
            path ?: ""})${if (path != null) "" else "?"})"
        }

        fun urlwithoutex(domain:String = domainPath,path:String? = null):String{
            return "(https?://$domain(?::\\d{2,5})?(?:[/\\?#]$urlChars${
            path ?: ""})${if (path != null) "" else "?"})"
        }

        fun urlwithoutexGroups(domain:String = domainPath,path:String? = null):String{
            return "(https?://($domain)((?::\\d{2,5})?(?:[/\\?#])$urlChars${
            path ?: ""})${if (path != null) "" else "?"})"
        }

        //RegexOption.IGNORE_CASE is "i"
        fun any(flags:RegexOption = RegexOption.IGNORE_CASE):Regex{

            return urlSet().toRegex(flags)
        }

        fun local(flags:RegexOption = RegexOption.IGNORE_CASE):Regex{
            return urlSet( "(?:localhost|(?:.*\\.)?steemit.com)").toRegex(flags)
        }

        fun remote(flags:RegexOption = RegexOption.IGNORE_CASE):Regex{
            return urlSet( "(?!localhost|(?:.*\\.)?steemit.com)$domainPath").toRegex(flags)
        }

        fun youTube(flags:RegexOption = RegexOption.IGNORE_CASE):Regex{
            return urlSet( "(?:(?:.*.)?youtube.com|youtu.be)").toRegex(flags)
        }

        fun image(flags:RegexOption = RegexOption.IGNORE_CASE):Regex{
            return urlSet( path = imagePath).toRegex(flags)
        }

        fun imageFile(flags:RegexOption = RegexOption.IGNORE_CASE):Regex{
            return imagePath.toRegex(flags)
        }

    }
}