package com.server.service

/**
 * Any service that should be initialized when our server is starting up.
 *
 * @author Tom <rspsmods@gmail.com>
 */
interface Service {

    /**
     * Called when the server is starting up.
     */
    fun init()

    /**
     * Called after the server has finished started up.
     */
    fun postLoad()

    /**
     * Called after the server sets its bootstrap's children and parameters.
     */
    fun bindNet()

    /**
     * Called when the server is shutting off.
     */
    fun terminate()
}