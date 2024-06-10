package com.soongan.soonganbackend

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JasyptTest {

    @Autowired
    @Qualifier(value = "jasyptStringEncryptor")
    private lateinit var jasyptStringEncryptor: PooledPBEStringEncryptor

    @Test
    fun test() {
        val encrypted = jasyptStringEncryptor.encrypt("여기에 암호화할 문자열을 넣고 실행하면 암호화된 문자열이 출력됩니다.")
        println(encrypted)
    }
}