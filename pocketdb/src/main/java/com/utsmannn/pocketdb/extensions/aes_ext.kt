package com.utsmannn.pocketdb.extensions

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val CHARSET_NAME = "UTF-8"
private const val CIPHER_ALGORITHM = "AES/CFB/PKCS5Padding"

fun String.decrypt(secretKey: String): String {
    val key = secretKey.toByteArray()
    val keySpec = SecretKeySpec(key, CIPHER_ALGORITHM)
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    val iv = key.copyOf(16)
    val ivSpec = IvParameterSpec(iv)
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
    val inputByte = toString().toByteArray(charset(CHARSET_NAME))
    return String(cipher.doFinal(Base64.decode(inputByte, Base64.DEFAULT)))
}

fun Any.encrypt(secretKey: String): String {
    val key = secretKey.toByteArray()
    val keySpec = SecretKeySpec(key, CIPHER_ALGORITHM)
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)

    val iv = key.copyOf(16)
    val ivSpec = IvParameterSpec(iv)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
    val inputByte = toString().toByteArray(charset(CHARSET_NAME))
    return String(Base64.encode(cipher.doFinal(inputByte), Base64.DEFAULT))
}