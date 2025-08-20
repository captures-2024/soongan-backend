package com.soongan.soonganbackend.soonganpersistence.util

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

class CustomFunctionContributor: FunctionContributor {

    companion object {
        const val DATETIME_CURSOR_FUNCTION_NAME = "datetime_cursor"
    }

    override fun contributeFunctions(functionContributions: FunctionContributions?) {
        functionContributions!!.functionRegistry
            .register(DATETIME_CURSOR_FUNCTION_NAME, StandardSQLFunction("DATE_FORMAT", StandardBasicTypes.STRING))
    }
}