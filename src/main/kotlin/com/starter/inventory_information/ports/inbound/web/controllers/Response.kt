package com.starter.inventory_information.ports.inbound.web.controllers

sealed class Response<out T> {
    data class Success<T>(val data: T) : Response<T>()
    data class Error(val message: String, val code: Int) : Response<Nothing>()
}