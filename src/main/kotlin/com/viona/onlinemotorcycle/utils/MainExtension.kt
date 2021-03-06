package com.viona.onlinemotorcycle.utils

import com.viona.onlinemotorcycle.base.BaseResponse
import com.viona.onlinemotorcycle.exception.OjolException

inline fun <reified T> T?.orThrow(
    message: String = "$(T::class.simpleName) is null"
): T {
    return this ?: throw OjolException("value is null")
}

inline fun <reified T> T?.toResult(
    message: String = "${T::class.simpleName} is null"
): Result<T> {
    return if (this != null) {
        Result.success(this)
    } else {
        Result.failure(OjolException(message))
    }
}

fun <T>Result<T>.toResponses(message: String = ""): BaseResponse<T> {
    return if (this.isFailure) {
        throw  OjolException(this.exceptionOrNull()?.message ?: "Failure")
    } else {
        BaseResponse.success(this.getOrNull())
    }
}