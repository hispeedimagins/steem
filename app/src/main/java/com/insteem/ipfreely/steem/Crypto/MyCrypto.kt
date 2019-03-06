package com.insteem.ipfreely.steem.Crypto

import com.insteem.ipfreely.steem.SteemBackend.Config.Models.TimePointSec
import com.insteem.ipfreely.steem.SteemBackend.Config.SteemJConfig
import com.insteem.ipfreely.steem.SteemBackend.Config.Utilities
import org.joou.UInteger
import org.joou.UShort

/**
 * Created by boot on 2/23/2018.
 */
class MyCrypto {

    fun GetRefBlockNumber(num:Int):UShort{

        return  UShort.valueOf(num and 0xffff)
    }

    fun GetRefBlockPrefix(id : String) : UInteger {
        var r : Ripemd160 = Ripemd160(id)
        return r.hashValue
    }

    fun GetExpirationTime() : TimePointSec{
        return TimePointSec(System.currentTimeMillis() + SteemJConfig.getInstance().getMaximumExpirationDateOffset() - 60000L)
    }



    fun toByteArray(name:String): ByteArray {
        return Utilities.transformStringToVarIntByteArray(name)
    }

    /*fun toByteArray(var uInteger: UInteger): ByteArray {
        try {
            ByteArrayOutputStream().use { serializedDateTime ->
                serializedDateTime.write(Utilities.transformIntToByteArray(this.getDateTimeAsInt()))

                return serializedDateTime.toByteArray()
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }*/


    /*fun Unhexilify(data : String): ByteArray {
        var d : ByteArray = BaseEncoding.base16().decode(data)
        return d
    }*/
}