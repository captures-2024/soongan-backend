package com.soongan.soonganbackend.config

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
@EnableEncryptableProperties
class Encryptor(
    private val env: Environment
) {

    @Bean("jasyptStringEncryptor")
    fun jasyptEncryptor(): PooledPBEStringEncryptor {
        return PooledPBEStringEncryptor().apply {
            setAlgorithm(env.getProperty("jasypt.encryptor.algorithm"))
            setPoolSize(env.getProperty("jasypt.encryptor.pool-size")!!.toInt())
            setStringOutputType(env.getProperty("jasypt.encryptor.string-output-type"))
            setKeyObtentionIterations(env.getProperty("jasypt.encryptor.key-obtention-iterations")!!.toInt())
            setPassword(env.getProperty("jasypt.encryptor.password"))
        }
    }
}