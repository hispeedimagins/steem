package com.insteem.ipfreely.steem.HelperClasses

class StripMarkDown {

    companion object {
        class mdOptions(){
            var listUnicodeChar:String = ""
            var stripListLeaders:Boolean = true
            var gfm:Boolean = true
        }
        fun stripMd(md:String,options:mdOptions):String{

            /*options = options || {};
            options.listUnicodeChar = options.hasOwnProperty('listUnicodeChar') ? options.listUnicodeChar : false;
            options.stripListLeaders = options.hasOwnProperty('stripListLeaders') ? options.stripListLeaders : true;
            options.gfm = options.hasOwnProperty('gfm') ? options.gfm : true;*/
            var output = "<br/>"
            //var output = md
            output += md

            //output = output.replace("^([\\s\t]*)([\\*\\-\\+]|\\d+\\.)\\s+", {mr -> mr.}  )

            //val check = "[^!?]\\[([-a-zA-Z0-9+&@#\\[\\]_\\-.!:,' ]*| |.)]\\((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\)"
            //val newtegex = "[!]\\[([-a-zA-Z0-9+&@#\\[\\]_. ]*(.\\w+)| |)]\\((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\)"
            //String regexmatch = "!\\[[\\w\\W]]\\([\\w\\W]\\)";
            // Remove horizontal rules (stripListHeaders conflict with this rule, which is why it has been moved to the top)
            //output = output.replace("^(-\\s*?|\\*\\s*?|_\\s*?){3,}\\s*$".toRegex(), "")

            try {
                if (options.stripListLeaders) {
                    if (options.listUnicodeChar != "")
                        output = output.replace("^([\\s\t]*)([*\\-+]|\\d+\\.)\\s+".toRegex(), options.listUnicodeChar + " ")
                        //output = output.replace("^([\\s\t]*)([\\*\\-\\+]|\\d+\\.)\\s+", options.listUnicodeChar + " $1")
                    else
                    //output = output.replace("/^([\\s\t]*)([\\*\\-\\+]|\\d+\\.)\\s+/gm", "$1")
                    output = output.replace("/^([\\s\t]*)([*\\-+]|\\d+\\.)\\s+/gm".toRegex(), " ")
                }
                if (options.gfm) {
                    output = output
                            // Header
                            //.replace("\n={2,}".toRegex(), "\n")
                    // Fenced codeblocks
                    .replace("~{3}.*\n".toRegex(), "")
                    // Strikethrough
                    .replace("~~".toRegex(), "")
                    // Fenced codeblocks
                    .replace("`{3}.*\n".toRegex(), "")
                }
                output = output

                        //Remove links from markdown
                        .replace(Links.any(),"")
                        // Remove HTML tags
                        .replace("<[^>]*>".toRegex(), "")
                // Remove setext-style headers
                //.replace("/^[=\\-]{2,}\\s*$".toRegex(), "")
                // Remove footnotes?
                //.replace("\\[\\^.+?](: .*?$)?".toRegex(), "")
                //.replace("\\s{0,2}\\[.*?]: .*?$".toRegex(), "")
                        //remove http
                        //.replace("/https?://[^ ]+/g".toRegex(), "")
                // Remove images
                .replace("!\\[.*?][\\[(].*?[])]".toRegex(), "")
                // Remove inline links
                .replace("\\[(.*?)][\\[(].*?[])]".toRegex(), "")
                //.replace("\\[(.*?)\\][\\[\\(].*?[\\]\\)]", "$1")
                // Remove blockquotes
                //.replace("^\\s{0,3}>\\s?".toRegex(), "")
                // Remove reference-style links?
                //.replace("^\\s{1,2}\\[(.*?)\\]: (\\S+)( ".*?")?\\s*$", "")
                // Remove atx-style headers
                //.replace("^(\n)?\\s*#{1,6}\\s+| *(\n)?\\s*#* *(\n)?\\s*$".toRegex(), "")
                //.replace("^(\n)?\\s{0,}#{1,6}\\s+| {0,}(\n)?\\s{0,}#{0,} {0,}(\n)?\\s{0,}$", "$1$2$3")
                // Remove emphasis (repeat the line to remove double emphasis)
                //.replace("([*_]{1,3})(\\S.*?\\S?)\\1".toRegex(), "")
                //.replace("([\\*_]{1,3})(\\S.*?\\S{0,1})\\1", "$2")
                //.replace("([*_]{1,3})(\\S.*?\\S?)\\1".toRegex(), "")
                //.replace("/[,!?]?\\s+[^\\s]+$/".toRegex(), "…")
                //.replace("([\\*_]{1,3})(\\S.*?\\S{0,1})\\1", "$2")
                // Remove code blocks
                //.replace("(`{3,})(.*?)\\1".toRegex(), "$2")
                // Remove inline code
                //.replace("`(.+?)`".toRegex(), "$1")
                // Replace two or more newlines with exactly two? Not entirely sure this belongs here...
                //.replace("\n{2,}".toRegex(), "\n\n")

                        //should remove any newlines more than 2
                .replace("\n{2,}".toRegex(), "")
                //.replace("/https?://[^ ]+/g".toRegex(), "")

                        //should remove all markdown # chars, so no headers
                .replace("[#]".toRegex(),"")
                //.replace(check.toRegex()," ")
                //.replace(newtegex.toRegex()," ")
                return output
            } catch(e:Exception) {
                //console.error(e);
                return md
            }
            //return output

        }
    }

}